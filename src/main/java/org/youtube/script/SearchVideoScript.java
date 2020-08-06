package org.youtube.script;

import org.openqa.selenium.WebDriver;
import org.youtube.AdRunnable;
import org.youtube.SpamViewProc;
import org.youtube.entities.ChannelVideo;
import org.youtube.entities.YoutubeAccount;
import org.youtube.google.GoogleScenario;
import org.youtube.util.CommonUtil;
import org.youtube.util.DriverUtil;
import org.youtube.util.ProxyUtil;
import org.youtube.youtube.YouTubeScenario;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import static org.youtube.util.LogUtil.info;
import static org.youtube.util.LogUtil.warning;

public class SearchVideoScript implements Runnable {

    public static final String AD_THREAD = "AD_THREAD";

    private final CountDownLatch countDownLatch;
    private final YoutubeAccount account;
    private final List<ChannelVideo> videos;
    private final String channelName;

    private WebDriver driver;
    private GoogleScenario googleScenario;
    private YouTubeScenario youTubeScenario;

    private Thread adThread;

    public SearchVideoScript(CountDownLatch countDownLatch, YoutubeAccount account, List<ChannelVideo> videos,
                             String channelName) {
        this.countDownLatch = countDownLatch;
        this.account = account;
        this.videos = videos;
        this.channelName = channelName;
    }

    @Override
    public void run() {
        String proxyName = ProxyUtil.findProxyForAccount(!account.isFake());
        driver = DriverUtil.initChrome(proxyName);
        googleScenario = new GoogleScenario(driver);
        youTubeScenario = new YouTubeScenario(driver);

        try {
            info("==================Start of acc " + account.getEmail() + "==================");
            if (!account.isFake()) {
                googleScenario.goGoogleSignInPageThrough3rdParty();
                googleScenario.attemptToLogin(account);
            }
            findingAds();

            /*
             * Dat lam 4 case cho nay
             * 1: tim kiem tu kenh ra, sau do vao danh sach clip, xem clip minh muon
             * 2: tim kiem theo ten clip, cung voi ten kenh
             * 3: vao thang url clip do
             * 4: xem bang danh sach phat tren youtube, trong qua trinh play tu dong hoac click bang tay trong list ben phai
             * */
            if (new Random().nextBoolean()) {
                // Xem video bằng cách nhập từ khoá search tên video
                for (ChannelVideo video : videos) {
                    youTubeScenario.attemptToSearchByVideoTitle(video.getTitle(), channelName);
                    CommonUtil.pause(5);
                    break;
                }
            } else {
                // Search tên channel
                youTubeScenario.attemptToSearchByChannelName(channelName.trim(), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            warning(e.getMessage());
            warning("Skip account " + account.getEmail());
            SpamViewProc.increaseFailedAccount();
        } finally {
            info("==================End of acc " + account.getEmail() + "==================");
            SpamViewProc.increaseNumberAttempt();
            stopFindingAds();
            countDownLatch.countDown();
            CommonUtil.pause(1);
            driver.quit();
        }
    }

    private void findingAds() {
        adThread = new Thread(new AdRunnable(driver, youTubeScenario, account.isFake()), AD_THREAD);
        adThread.start();
    }

    private void stopFindingAds() {
        if (adThread != null && adThread.isAlive()) {
            adThread.interrupt();
        }
    }

}
