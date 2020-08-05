package org.youtube;

import org.openqa.selenium.WebDriver;
import org.youtube.entities.ChannelVideo;
import org.youtube.entities.YoutubeAccount;
import org.youtube.google.GoogleScenario;
import org.youtube.util.CommonUtil;
import org.youtube.util.DriverUtil;
import org.youtube.youtube.YouTubeScenario;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import static org.youtube.util.LogUtil.info;
import static org.youtube.util.LogUtil.warning;

public class MainRunnable implements Runnable {

    public static final String AD_THREAD = "AD_THREAD";

    private final CountDownLatch countDownLatch;
    private final YoutubeAccount account;
    private final List<ChannelVideo> videos;

    private WebDriver driver;
    private GoogleScenario googleScenario;
    private YouTubeScenario youTubeScenario;
    private final boolean isSpamView;
    private final boolean needEthernet;

    private Thread adThread;

    public MainRunnable(CountDownLatch countDownLatch, YoutubeAccount account,
                        List<ChannelVideo> videos, boolean isSpamView, boolean needEthernet) {
        this.countDownLatch = countDownLatch;
        this.account = account;
        this.videos = videos;
        this.isSpamView = isSpamView;
        this.needEthernet = needEthernet;
    }

    @Override
    public void run() {
        String proxyName = findProxyForAccount();
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
            Collections.shuffle(videos);
            for (ChannelVideo video : videos) {
                info("video : " + video.getVideoUrl());
                youTubeScenario.openLink(video);
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

    private String findProxyForAccount() {
        boolean needEthernet = !account.isFake();
        if (needEthernet)
            return "proxy_full_" + new Random().nextInt(3);
        else
            return "proxy_" + new Random().nextInt(6);
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
