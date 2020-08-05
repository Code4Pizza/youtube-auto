package org.youtube;

import io.vavr.collection.Stream;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.youtube.entities.ChannelVideo;
import org.youtube.entities.YoutubeAccount;
import org.youtube.entities.YoutubeChannel;
import org.youtube.script.SearchVideoScript;
import org.youtube.storage.YoutubeDatabases;
import org.youtube.util.LogUtil;
import org.youtube.util.StorageUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.youtube.util.LogUtil.info;

public class SpamViewProc {

    ExecutorService executor = Executors.newFixedThreadPool(3);

    protected final YoutubeDatabases youtubeDatabases = StorageUtil.getAccDatabase();

    public static volatile int failedAccount = 0;
    public static volatile int numberAttempt = 0;

    protected static synchronized void resetCount() {
        failedAccount = 0;
        numberAttempt = 0;
    }

    public static synchronized void increaseFailedAccount() {
        failedAccount++;
    }

    public static synchronized void increaseNumberAttempt() {
        numberAttempt++;
    }

    public void playMainScenario(boolean isSpamView) {
        resetCount();

        List<YoutubeAccount> youtubeAccounts = youtubeDatabases.getAllAccounts();
        info("Total number accounts is " + youtubeAccounts.size());

        if (isSpamView) {
            List<YoutubeAccount> fakeAccounts = createFakeAccount(youtubeAccounts.size() * 5);
            youtubeAccounts = Stream.concat(youtubeAccounts, fakeAccounts).collect(Collectors.toList());
            Collections.shuffle(youtubeAccounts);
        }

        CountDownLatch countDownLatch = new CountDownLatch(youtubeAccounts.size());

        for (YoutubeAccount youtubeAccount : youtubeAccounts) {
            // moi account se chay mot thread
            Pair<String, List<ChannelVideo>> videos = prepareVideos();
            executor.execute(new SearchVideoScript(
                    countDownLatch,
                    youtubeAccount,
                    videos.getRight(),
                    videos.getLeft()));
            break;
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            info("Main Scenario finished");
            info("Total attempt " + numberAttempt);
            info("Number failed acc " + failedAccount);
            // tim xem con thang nao dang chay khong thi kill di nhe
            Process process;
            try {
                process = Runtime.getRuntime().exec("kill -9 $(pgrep -f chromedriver)");
                process.destroy();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Pair<String, List<ChannelVideo>> prepareVideos() {
        List<YoutubeChannel> channels = youtubeDatabases.getAllChannels();
        YoutubeChannel channel = channels.get(0);
        return new ImmutablePair<>(channel.getChannelName(), youtubeDatabases.getAllChannelVideos(channel));
    }

    public List<YoutubeAccount> createFakeAccount(int counter) {
        List<YoutubeAccount> fakeAccounts = new ArrayList<>();
        for (int i = 0; i < counter; i++) {
            fakeAccounts.add(YoutubeAccount.createFakeAccount());
        }
        return fakeAccounts;
    }

    public static void main(String[] args) {
        LogUtil.init("main_log");
        SpamViewProc mainScript = new SpamViewProc();
        mainScript.playMainScenario(false);
    }
}
