package org.youtube;

import io.vavr.collection.Stream;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.webfilm.QueryVideosJob;
import org.webfilm.api.ApiService;
import org.webfilm.api.RetryException;
import org.webfilm.api.RunOutKeyException;
import org.webfilm.entity.Channel;
import org.webfilm.entity.ParsedConfig;
import org.webfilm.entity.Video;
import org.webfilm.storage.WebFilmDatabase;
import org.youtube.entities.ChannelVideo;
import org.youtube.entities.YoutubeAccount;
import org.youtube.entities.YoutubeChannel;
import org.youtube.script.SearchVideoScript;
import org.youtube.storage.YoutubeDatabases;
import org.youtube.util.FilesUtil;
import org.youtube.util.LogUtil;
import org.youtube.util.StorageUtil;

import java.io.File;
import java.io.FileNotFoundException;
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
    public static SpamViewProc INSTANCE;

    public static SpamViewProc getInstance() {
        if (INSTANCE == null) {
            WebFilmDatabase database = WebFilmDatabase.getInstance();
            ParsedConfig configs = database.getSystemConfigs();
            ApiService apiService = new ApiService(configs);
            INSTANCE = new SpamViewProc(configs, apiService, database);
        }
        return INSTANCE;
    }


    private final ParsedConfig configs;
    private final ApiService apiService;
    private final WebFilmDatabase database;


    private SpamViewProc(ParsedConfig configs, ApiService apiService, WebFilmDatabase database) {
        this.configs = configs;
        this.apiService = apiService;
        this.database = database;
    }

    ExecutorService executor = Executors.newFixedThreadPool(1);


    public static volatile int failedAccount = 0;
    public static volatile int numberAttempt = 0;
    protected final YoutubeDatabases youtubeDatabases = StorageUtil.getAccDatabase();

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

    public void playMainScenario(boolean isSpamView) throws RunOutKeyException, IOException, RetryException {
        resetCount();

        List<YoutubeAccount> youtubeAccounts = FilesUtil.getAccountsFromFile("100_mails.txt");
        info("Total number accounts is " + youtubeAccounts.size());

        if (isSpamView) {
            List<YoutubeAccount> fakeAccounts = createFakeAccount(youtubeAccounts.size() * 5);
            youtubeAccounts = Stream.concat(youtubeAccounts, fakeAccounts).collect(Collectors.toList());
            Collections.shuffle(youtubeAccounts);
        }

        CountDownLatch countDownLatch = new CountDownLatch(youtubeAccounts.size());

        for (YoutubeAccount youtubeAccount : youtubeAccounts) {
            // moi account se chay mot thread
            Pair<String, List<Video>> videos = prepareVideos();
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

    public Pair<String, List<Video>> prepareVideos() throws RetryException, RunOutKeyException, IOException {
        List<String> channelIds = FilesUtil.getChannelsFromFile("channels.txt");
        StringBuilder listQueryChannelId = new StringBuilder();
        for (int i = 0; i < channelIds.size(); i++) {
            listQueryChannelId.append(channelIds.get(i));
            if (i != channelIds.size() - 1) {
                listQueryChannelId.append(",");
            }
        }
        List<Channel> channels = apiService.getChannelInfos(listQueryChannelId.toString());
        Channel channel = channels.get(1);
        List<Video> videos = FilesUtil.getVideoOfChannelFromFile("videos.txt");
        return new ImmutablePair<>(channel.getName(), videos);
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
        SpamViewProc mainScript = getInstance();
        try {
            mainScript.playMainScenario(false);
        } catch (IOException | RetryException | RunOutKeyException e) {
            e.printStackTrace();
        }

    }
}
