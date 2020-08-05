package org.youtube;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.youtube.util.CommonUtil;
import org.youtube.youtube.YouTubeException;
import org.youtube.youtube.YouTubeScenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.youtube.util.LogUtil.info;

public class AdRunnable implements Runnable {

    private final boolean byPassClick;
    private final WebDriver driver;
    private final YouTubeScenario youTubeScenario;

    private int clickedAds, totalAds;

    public AdRunnable(WebDriver driver, YouTubeScenario youTubeScenario, boolean byPassClick) {
        this.driver = driver;
        this.youTubeScenario = youTubeScenario;
        this.byPassClick = byPassClick;
    }

    @Override
    public void run() {
        while (true) {
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
        if (byPassClick)
            return false;
        info(String.format("Start check can click ads with total %d, clicked %d", totalAds, clickedAds));
        if (clickedAds == 0 || totalAds == 0) {
            return true;
        } else {
            int percent = clickedAds * 100 / totalAds;
            return percent <= 15;
        }
    }
}
