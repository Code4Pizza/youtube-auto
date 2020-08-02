package org.youtube;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.youtube.entities.ChannelVideo;
import org.youtube.entities.YoutubeAccount;
import org.youtube.google.GoogleScenario;
import org.youtube.storage.YoutubeDatabases;
import org.youtube.util.CommonUtil;
import org.youtube.util.DriverUtil;
import org.youtube.youtube.YouTubeException;
import org.youtube.youtube.YouTubeScenario;

import java.util.*;
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
    private YoutubeDatabases databases;
    private YouTubeScenario youTubeScenario;
    private final boolean isSpamView;
    private final boolean needEthernet;

    private Thread adThread;
    private boolean runningAd;

    public MainRunnable(CountDownLatch countDownLatch, YoutubeAccount account,
                        YoutubeDatabases databases,
                        List<ChannelVideo> videos, boolean isSpamView, boolean needEthernet) {
        this.countDownLatch = countDownLatch;
        this.account = account;
        this.videos = videos;
        this.isSpamView = isSpamView;
        this.needEthernet = needEthernet;
        this.databases = databases;
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
            for (ChannelVideo video : videos.subList(0, 8)) {
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
//        boolean needEthernet =  !account.isFake();
//        if (needEthernet)
            return "proxy_full_" + new Random().nextInt(4);
//        else
//            return "proxy_" + new Random().nextInt(6);
    }

    private void findingAds() {
        runningAd = true;
        adThread = new Thread(new AdRunnable(account.isFake()), AD_THREAD);
        adThread.start();
    }

    private void stopFindingAds() {
        runningAd = false;
        if (adThread != null && adThread.isAlive()) {
            adThread.interrupt();
        }
    }

    public class AdRunnable implements Runnable {

        private final boolean byPassClick;

        public AdRunnable(boolean byPassClick) {
            this.byPassClick = byPassClick;
        }

        private int clickedAds, totalAds;

        @Override
        public void run() {
            while (runningAd) {
                info("Waiting ad show...");
                waitingPreAdToClick();
                waitingMiddleSmallAdToClick();
                waitingSuperSmallAdToClick();
                CommonUtil.pause(30);
            }
        }

        private void waitingPreAdToClick() {
            WebElement adButton = CommonUtil.waitAdShow(driver, "ytp-ad-button-text", null);
            if (adButton != null) {
                info(String.format("Found %d ads", ++totalAds));
                if (canClick(totalAds, clickedAds)) {
                    CommonUtil.pause(new Random().nextInt(3) + 1);

                    Actions actions = new Actions(driver);
                    actions.moveToElement(adButton).click().perform();

                    CommonUtil.pause(5);

                    Set<String> sets = driver.getWindowHandles();
                    List<String> lists = new ArrayList<>(sets);

                    if (lists.size() > clickedAds + 1) {
                        info(String.format("Clicked %d of %d display ads", ++clickedAds, totalAds));
                        driver.switchTo().window(lists.get(0));
                        CommonUtil.pause(1);
                        try {
                            youTubeScenario.attemptToPlay();
                            // cho nay tim xem nut bo qua quang cao o dau
                            WebElement skipButton = CommonUtil.waitElement(driver, "ytp-ad-skip-button", null);
                            if (skipButton != null) {
                                actions.moveToElement(skipButton).click().perform();
                            } else {
                                info("Can not find skip button");
                            }
                        } catch (YouTubeException.YouTubeFailedToPlayException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    info("Can not click this ads, find skip button");
                    // cho nay tim xem nut bo qua quang cao o dau
                    WebElement skipButton = CommonUtil.waitElement(driver, "ytp-ad-skip-button", null);
                    if (skipButton != null) {
                        Actions actions = new Actions(driver);
                        actions.moveToElement(skipButton).click().perform();
                    } else {
                        info("Can not find skip button");
                    }
                }
            } else {
                info("Time out waiting ads full screen  ");
            }
        }

        private void waitingMiddleSmallAdToClick() {
            // tim kiem thu may the quang cao nho thoi
            WebElement smallAds = CommonUtil.waitElement(driver, "ytp-ad-overlay-image", null);
            if (smallAds != null) {
                info(String.format("Found a small button, now found %d ads, clicked %d ads", ++totalAds, clickedAds));
                if (canClick(totalAds, clickedAds)) {
                    new Actions(driver).moveToElement(smallAds).click().perform();
                    Set<String> sets = driver.getWindowHandles();
                    List<String> lists = new ArrayList<>(sets);

                    if (lists.size() > clickedAds + 1) {
                        info(String.format("Clicked %d of %d display ads", ++clickedAds, totalAds));
                        driver.switchTo().window(lists.get(0));
                        CommonUtil.pause(1);
                        try {
                            youTubeScenario.attemptToPlay();

                            // dong quang cao nay lai
                            WebElement skipButton = CommonUtil.waitElement(driver, "ytp-ad-overlay-close-button", null);
                            if (skipButton != null) {
                                Actions actions = new Actions(driver);
                                actions.moveToElement(skipButton).click().perform();
                            } else {
                                info("Can not find close button");
                            }
                        } catch (YouTubeException.YouTubeFailedToPlayException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    info("Now can not click this small ads, wait for next time");
                    // dong quang cao nay lai
                    WebElement skipButton = CommonUtil.waitElement(driver, "ytp-ad-overlay-close-button", null);
                    if (skipButton != null) {
                        Actions actions = new Actions(driver);
                        actions.moveToElement(skipButton).click().perform();
                    } else {
                        info("Can not find close button");
                    }
                }
            } else {
                info("Now can not find any small ads");
            }
        }

        private void waitingSuperSmallAdToClick() {
            // tim kiem thu may the quang cao sieu nho thoi
            WebElement tiniAds = CommonUtil.waitElement(driver, "ytp-ad-overlay-link", null);
            if (tiniAds != null) {
                info(String.format("Found a tini button, now found %d ads, clicked %d ads", ++totalAds, clickedAds));
                if (canClick(totalAds, clickedAds)) {
                    new Actions(driver).moveToElement(tiniAds).click().perform();
                    Set<String> sets = driver.getWindowHandles();
                    List<String> lists = new ArrayList<>(sets);

                    if (lists.size() > clickedAds + 1) {
                        info(String.format("Clicked %d of %d display ads", ++clickedAds, totalAds));
                        driver.switchTo().window(lists.get(0));
                        CommonUtil.pause(1);
                        try {
                            youTubeScenario.attemptToPlay();
                            // dong quang cao nay lai
                            WebElement skipButton = CommonUtil.waitElement(driver, "ytp-ad-overlay-close-button", null);
                            if (skipButton != null) {
                                Actions actions = new Actions(driver);
                                actions.moveToElement(skipButton).click().perform();
                            } else {
                                info("Can not find close button");
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } else {
                    info("Now can not click this tini ads, wait for next time");
                    // dong quang cao nay lai
                    WebElement skipButton = CommonUtil.waitElement(driver, "ytp-ad-overlay-close-button", null);
                    if (skipButton != null) {
                        Actions actions = new Actions(driver);
                        actions.moveToElement(skipButton).click().perform();
                    } else {
                        info("Can not find close button");
                    }
                }
            } else {
                info("Now can not find any tini ads");
            }
        }

        private boolean canClick(int totalAds, int clickedAds) {
//            if (byPassClick)
//                return false;
            info(String.format("Start check can click ads with total %d, clicked %d", totalAds, clickedAds));
            if (clickedAds == 0 || totalAds == 0) {
                return true;
            } else {
                int percent = clickedAds * 100 / totalAds;
                return percent <= 25;
            }
        }
    }
}
