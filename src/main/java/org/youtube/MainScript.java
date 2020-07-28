package org.youtube;

import org.jdbi.v3.core.Jdbi;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.youtube.configuration.CircuitBreakerConfiguration;
import org.youtube.entities.ChannelVideo;
import org.youtube.entities.YoutubeAccount;
import org.youtube.entities.YoutubeChannel;
import org.youtube.google.GoogleScenario;
import org.youtube.storage.YoutubeDatabases;
import org.youtube.storage.FaultTolerantDatabase;
import org.youtube.util.CommonUtil;
import org.youtube.util.DriverUtil;
import org.youtube.util.LogUtil;
import org.youtube.youtube.YouTubeException;
import org.youtube.youtube.YouTubeScenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.youtube.util.Constants.*;
import static org.youtube.util.LogUtil.*;

public class MainScript {

    private final GoogleScenario googleScenario;
    private final YouTubeScenario youtubeScenario;
    private final YoutubeDatabases youtubeDatabases;

    public MainScript(GoogleScenario googleScenario, YouTubeScenario youtubeScenario, YoutubeDatabases youtubeDatabases) {
        this.googleScenario = googleScenario;
        this.youtubeScenario = youtubeScenario;
        this.youtubeDatabases = youtubeDatabases;
    }


    private Thread adThread;

    private Integer totalAds = 0;
    private Integer clickedAds = 0;

    private void findingAds() {
        adThread = new Thread(runable);
        adThread.start();
    }

    private Runnable runable = new Runnable() {

        @Override
        public void run() {
            WebDriver driver = DriverUtil.getInstance();
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
                            youtubeScenario.attempToPlay();
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
                info("Found a small button");
                if (canClick(++totalAds, clickedAds)) {
                    new Actions(driver).moveToElement(smallAds).click().perform();
                    Set<String> sets = driver.getWindowHandles();
                    List<String> lists = new ArrayList<>(sets);

                    if (lists.size() > clickedAds + 1) {
                        info(String.format("Clicked %d of %d display ads", ++clickedAds, totalAds));
                        driver.switchTo().window(lists.get(0));
                        CommonUtil.pause(1);
                        try {
                            youtubeScenario.attempToPlay();
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
            CommonUtil.pause(30);

            adThread = new Thread(this);
            adThread.start();

        }
    };

    private boolean canClick(Integer totalAds, Integer clickedAds) {
        if (clickedAds == 0 || totalAds == 0)
            return true;
        else return (float) clickedAds / totalAds < 0.1;
    }

    public static YoutubeDatabases getAccDatabase() {
        Jdbi jdbi = Jdbi.create(BASE_URL, USER_NAME, PASSWORD);
        FaultTolerantDatabase accDatabase = new FaultTolerantDatabase(DB_NAME, jdbi, new CircuitBreakerConfiguration());
        return new YoutubeDatabases(accDatabase);
    }

    public int numberAttempt = 0;
    public int failedAccount = 0;

    public void playScenario4() {
        findingAds();
        numberAttempt = 0;
        failedAccount = 0;
        List<YoutubeAccount> youtubeAccounts = youtubeDatabases.getAllAccounts();
        for (YoutubeAccount youtubeAccount : youtubeAccounts) {
            try {
                googleScenario.goGoogleSignInPage();
//                googleScenario.goGoogleSignInPageThrough3rdParty();
                googleScenario.attemptToLogin(youtubeAccount);

                if (adThread != null) {
                    System.out.println("Interupt Ads");
                    adThread.interrupt();
                }


                List<YoutubeChannel> channels = youtubeDatabases.getAllChannels();
                for (YoutubeChannel channel : channels) {
                    List<ChannelVideo> videos = youtubeDatabases.getAllChannelVideos(channel);
                    videos.forEach(video -> {
                        try {
                            youtubeScenario.openLink(video.getVideoUrl());
                        } catch (YouTubeException.YouTubeFailedToPlayException e) {
                            warning(e.getMessage());
                        }
                    });
                }

                googleScenario.attemptSignOut();
            } catch (Exception e) {
                severe(e.getMessage());
                severe("Skip account " + youtubeAccount.getEmail());
                failedAccount++;
            }
            info("==================End of acc flow=================");
            numberAttempt++;
//            CommonUtil.pause(20);
        }
        info("Scenario 4 finished");
        info("Number failed acc " + failedAccount);
    }

    public static void main(String[] args) {
        LogUtil.init();

        WebDriver driver = DriverUtil.getInstance();
        GoogleScenario googleScenario = new GoogleScenario(driver);
        YouTubeScenario youtubeScenario = new YouTubeScenario(driver);

        YoutubeDatabases youtubeDatabases = getAccDatabase();

        MainScript mainScript = new MainScript(googleScenario, youtubeScenario, youtubeDatabases);

        try {
            mainScript.playScenario4();
        } catch (Exception e) {
            e.printStackTrace();
            severe("Scenario suspend unexpectedly");
            severe("Number attempt" + mainScript.numberAttempt);
            severe("Failed account " + mainScript.failedAccount);
        }


    }
}
