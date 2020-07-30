package org.youtube;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.youtube.entities.ChannelVideo;
import org.youtube.entities.YoutubeAccount;
import org.youtube.entities.YoutubeChannel;
import org.youtube.google.GoogleScenario;
import org.youtube.storage.YoutubeDatabases;
import org.youtube.util.CommonUtil;
import org.youtube.util.DriverUtil;
import org.youtube.util.LogUtil;
import org.youtube.util.StorageUtil;
import org.youtube.youtube.YouTubeException;
import org.youtube.youtube.YouTubeScenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static org.youtube.util.LogUtil.*;

public class MainScript {

    private final YoutubeDatabases youtubeDatabases;


    public int numberAttempt = 0;
    public int failedAccount = 0;

    public MainScript(YoutubeDatabases youtubeDatabases) {
        this.youtubeDatabases = youtubeDatabases;
    }

    private void resetCount() {
        numberAttempt = 0;
        failedAccount = 0;
    }

    private boolean canClick(Integer totalAds, Integer clickedAds) {
        info(String.format("Start check can click ads with total %d, clicked %d", totalAds, clickedAds));
        if (clickedAds == 0 || totalAds == 0)
            return true;
        else {
            int percent = clickedAds * 100 / totalAds;
            return percent < 30;
        }
    }

//    public void playScenario4() {
//        findingAds();
//        numberAttempt = 0;
//        failedAccount = 0;
//        List<YoutubeAccount> youtubeAccounts = youtubeDatabases.getAllAccounts();
//        for (int i = 0; i < youtubeAccounts.size(); i++) {
//            info("Start run account number " + i);
//            YoutubeAccount youtubeAccount = youtubeAccounts.get(i);
//            try {
////                googleScenario.goGoogleSignInPage();
//                googleScenario.goGoogleSignInPageThrough3rdParty();
//                googleScenario.attemptToLogin(youtubeAccount);
//
//                List<YoutubeChannel> channels = youtubeDatabases.getAllChannels();
//                for (YoutubeChannel channel : channels) {
//                    List<ChannelVideo> videos = youtubeDatabases.getAllChannelVideos(channel);
//                    videos.forEach(video -> {
//                        youtubeScenario.openLink(video);
//                    });
//                }
//
//                googleScenario.attemptSignOut();
////                googleScenario.attemptToSignOutYouTube();
//            } catch (Exception e) {
//                if (e instanceof WebDriverException) {
//                    severe("Browser suspend unexpectedly, " + e.getMessage());
//                    break;
//                } else {
//                    severe(e.getMessage());
//                    severe("Skip account " + youtubeAccount.getEmail() + " number " + i);
//                    failedAccount++;
//                }
//            }
//            info("==================End of acc flow=================");
//            numberAttempt++;
//            CommonUtil.pause(2);
//        }
//        info("Scenario 4 finished");
//        info("Number attempt " + numberAttempt);
//        info("Number failed acc " + failedAccount);
//        if (adThread != null) {
//            adThread.interrupt();
//        }
//    }

    public void playMainScenario() {
        resetCount();
        List<YoutubeAccount> youtubeAccounts = youtubeDatabases.getAllAccounts();
        info("Total number accounts is " + youtubeAccounts.size());
        CountDownLatch countDownLatch = new CountDownLatch(youtubeAccounts.size());
        List<YoutubeChannel> channels = youtubeDatabases.getAllChannels();
        YoutubeChannel channel = channels.get(0);
        List<ChannelVideo> videos = youtubeDatabases.getAllChannelVideos(channel);
        for (int i = 0; i < youtubeAccounts.size(); i++) {
            new Thread(new MainRunnable(countDownLatch, youtubeAccounts.get(i), videos)).start();
        }
//        stopFindingAds();

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            info("Main Scenario finished");
            info("Number attempt " + numberAttempt);
            info("Number failed acc " + failedAccount);
        }
    }


    public class MainRunnable implements Runnable {


        private final Runnable runnable = new Runnable() {

            @Override
            public void run() {
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
                                youTubeScenario.attempToPlay();
                                // cho nay tim xem nut bo qua quang cao o dau
                                WebElement skipButton = CommonUtil.waitElement(driver, "ytp-ad-skip-button", null);
                                if (skipButton != null) {

                                    actions.moveToElement(skipButton).click().perform();
                                } else {
                                    info("Can not find skip button");
                                }
                            } catch (YouTubeException.YouTubeFailedToPlayException e) {
                                // TODO Auto-generated catch block
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
                    // driver.close();
                } else {
                    info("Time out waiting ads full screen  ");
                }

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
                                youTubeScenario.attempToPlay();
                            } catch (YouTubeException.YouTubeFailedToPlayException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    } else {
                        info("Now can not click this small ads, wait for next time");
                    }
                } else {
                    info("Now can not find any small ads");
                }


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
                                youTubeScenario.attempToPlay();
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
                    }
                } else {
                    info("Now can not find any tini ads");
                }
                CommonUtil.pause(30);

                adThread = new Thread(this);
                adThread.start();
            }
        };


        private Thread adThread;
        private Integer totalAds = 0;
        private Integer clickedAds = 0;


        private final WebDriver driver;
        private final GoogleScenario googleScenario;
        private final YouTubeScenario youTubeScenario;

        private final YoutubeAccount youtubeAccount;

        private final CountDownLatch countDownLatch;

        private final List<ChannelVideo> videos;


        public MainRunnable(CountDownLatch countDownLatch, YoutubeAccount youtubeAccount, List<ChannelVideo> videos) {
            this.youtubeAccount = youtubeAccount;
            this.driver = DriverUtil.initChrome();
            this.googleScenario = new GoogleScenario(driver);
            this.youTubeScenario = new YouTubeScenario(driver);
            this.countDownLatch = countDownLatch;
            this.videos = videos;
        }

        @Override
        public void run() {
            try {
                findingAds();
                googleScenario.goGoogleSignInPageThrough3rdParty();
                googleScenario.attemptToLogin(youtubeAccount);
                for (ChannelVideo video : videos) {
                    info("video : " + video.getVideoUrl());
                    youTubeScenario.openLink(video);
                }

            } catch (Exception e) {
                if (e instanceof WebDriverException) {
                    severe("Browser suspend unexpectedly, " + e.getMessage());
                } else {
                    e.printStackTrace();
                    severe(e.getMessage());
                    warning("Skip account " + youtubeAccount.getEmail());
                    failedAccount++;
                }
            } finally {
                numberAttempt++;
                info("==================End of acc flow=================");
                stopFindingAds();
                countDownLatch.countDown();
                CommonUtil.pause(2);
                driver.quit();
            }
        }

        private void findingAds() {
            adThread = new Thread(runnable);
            adThread.start();
        }

        private void stopFindingAds() {
            if (adThread != null) {
                adThread.interrupt();
            }
        }

    }

    public static void main(String[] args) {
        LogUtil.init();

//        WebDriver driver = DriverUtil.initChrome();
//        GoogleScenario googleScenario = new GoogleScenario(driver);
//        YouTubeScenario youtubeScenario = new YouTubeScenario(driver);
//        YoutubeDatabases youtubeDatabases = StorageUtil.getAccDatabase();

        MainScript mainScript = new MainScript(StorageUtil.getAccDatabase());
        mainScript.playMainScenario();

//        try {
//            mainScript.playScenario4();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            DriverUtil.close();
//        }
    }
}
