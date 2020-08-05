package org.youtube.youtube;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.youtube.entities.ChannelVideo;
import org.youtube.util.CommonUtil;
import org.youtube.util.Constants;
import org.youtube.util.LogUtil;

import java.util.List;
import java.util.Random;

import static org.youtube.util.CommonUtil.scrollDown;
import static org.youtube.util.LogUtil.*;

public class YouTubeScenario {

    public static final String YOUTUBE_URL = "https://www.youtube.com/";
    private static final Logger logger = LoggerFactory.getLogger(YouTubeScenario.class);

    public static final long DEFAULT_DELAY = 15 * 60 * 1000;

    private final WebDriver driver;

    public YouTubeScenario(WebDriver driver) {
        LogUtil.init("youtube_log");
        this.driver = driver;
    }

    public void openLink(ChannelVideo video) throws YouTubeException.YouTubeFailedToPlayException {
        String url = video.getVideoUrl();
        logger.info("Open url " + url);
        driver.get(url);

        attemptToPlay();

        // sleep khoang 50% thoi luong clip
        try {

            Thread.sleep(Math.min(video.getDuration() * 400, DEFAULT_DELAY / 2));
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        attemptToLike();
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        attemptToSubscribe();
        try {

            Thread.sleep(Math.min(video.getDuration() * 400, DEFAULT_DELAY / 2));
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void attemptToPlay() throws YouTubeException.YouTubeFailedToPlayException {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Constants.DEFAULT_DELAY_SECOND);
            wait.until(ExpectedConditions.elementToBeClickable(By.className("ytp-play-button")));
            WebElement playElement = driver.findElement(By.className("ytp-play-button"));
            if (playElement != null) {
                logger.info("Found play element, now need to find play button");
                String titleButtonPlay = playElement.getAttribute("title");
                if (titleButtonPlay.contains("Phát") || titleButtonPlay.contains("Play")) {
                    logger.info("Click play video");
                    CommonUtil.click(playElement);
                } else {
                    logger.info("Cant find play buton");
                }
            } else {
                logger.info("Can not find play button");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new YouTubeException.YouTubeFailedToPlayException();
        }
    }

    private void attemptToLike() {
        CommonUtil.pause(5);
        List<WebElement> elements = driver.findElements(By.xpath("//*[@id=\"button\"]"));
        for (WebElement e : elements) {
            String ariaLabel = e.getAttribute("aria-label");
            String ariaPressed = e.getAttribute("aria-pressed");
            if (ariaLabel == null || ariaPressed == null) {
                continue;
            }
            if (ariaLabel.contains("khác thích video này") && ariaPressed.equals("false")) {
                logger.info("Click like");
                e.click();
                break;
            }
        }
    }

    private void attemptToSubscribe() {
        CommonUtil.pause(5);
        List<WebElement> subElements = driver.findElements(By.className("ytd-subscribe-button-renderer"));
        for (WebElement e : subElements) {
            if ("ĐĂNG KÝ".toLowerCase().equals(e.getText().toLowerCase())
                    || ("Subscribe".toLowerCase().equals(e.getText().toLowerCase()))) {
                logger.info("Click subscribe");
                e.click();
                break;
            }
        }
    }

    public void attemptToSearchByVideoTitle(String title, String channelName) {
        try {
            driver.get(YOUTUBE_URL);

            WebElement searchInput = CommonUtil.waitSearchShow(driver);
            if (searchInput == null) {
                severe("Không tìm thấy ô tìm kiếm !");
                return;
            }

            enterSearchKey(searchInput, title);

            List<WebElement> videoTitles = driver.findElements(By.id("video-title"));
            List<WebElement> channels = driver.findElements(By.id("channel-name"));

            Pair<Pair<Integer, WebElement>, WebElement> pair = findChannelInSearchResultScreen(
                    channels,
                    videoTitles,
                    channelName
            );

            CommonUtil.pause(1);

            loadAndClickVideoSearched(3, pair, channelName);

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public Pair<Pair<Integer, WebElement>, WebElement> findChannelInSearchResultScreen(
            List<WebElement> channels,
            List<WebElement> videoTitles,
            String channelName) {

        WebElement channelElement = null;
        WebElement titleElement = null;
        int channelIndex = 0;
        int titleIndex = 0;

        for (WebElement element : channels) {
            if (!element.getText().isEmpty()) {
                try {
                    // Tìm span để tránh count index cái clip mix
                    WebElement span = element.findElement(By.className("yt-simple-endpoint"));
                } catch (NoSuchElementException ignored) {
                    continue;
                }
                if (element.getText().equals(channelName)) {
                    warning("Index " + channelIndex + " Channel " + element.getText());
                    channelElement = element;
                    break;
                }
                channelIndex++;
            }
        }
        for (WebElement element : videoTitles) {
            if (!element.getText().isEmpty() && element.getAttribute("href") != null) {
                if (channelElement != null && channelIndex == titleIndex) {
                    titleElement = element;
                    warning("Index " + titleIndex + " Title " + element.getText());
                    break;
                }
                titleIndex++;
            }
        }

        if (channelElement != null) {
            Pair<Integer, WebElement> eChannel = new ImmutablePair<>(channelIndex, channelElement);
            Pair<Integer, WebElement> eTitle = new ImmutablePair<>(titleIndex, titleElement);
            return new ImmutablePair<>(eChannel, titleElement);
        }
        return null;
    }

    public void loadAndClickVideoSearched(int numberAttempt, Pair<Pair<Integer, WebElement>, WebElement> pair, String channelName) {
        if (pair == null) {
            warning("Chưa thấy kênh cần tìm");
            if (numberAttempt == 0) {
                warning("Kết thúc tìm kiếm, không có kết quả");
                return;
            }
            warning("Số lần thử tìm còn lại : " + numberAttempt);

            // Loadmore
            for (int i = 0; i < 4; i++) {
                scrollDown(driver);
            }

            List<WebElement> videoTitles = driver.findElements(By.id("video-title"));
            List<WebElement> channels = driver.findElements(By.id("channel-name"));

            Pair<Pair<Integer, WebElement>, WebElement> newPair = findChannelInSearchResultScreen(channels, videoTitles, channelName);
            loadAndClickVideoSearched(--numberAttempt, newPair, channelName);
        } else {
            warning("Tìm thấy kênh");

            int channelIndex = pair.getLeft().getLeft();
            WebElement channelElement = pair.getLeft().getRight();
            WebElement titleElement = pair.getRight();

            for (int i = 0; i < (channelIndex - (numberAttempt * 4)) / 8; i++) {
                scrollDown(driver);
            }

            CommonUtil.pause(1);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView();", channelElement);
            CommonUtil.pause(1);

            if (titleElement != null) {
                info("Click open video");
                Actions actions = new Actions(driver);
                actions.moveToElement(titleElement).click().perform();
            }
        }

    }

    public void attemptToSearchByChannelName(String channelName) {
        driver.get(YOUTUBE_URL);

        WebElement searchInput = CommonUtil.waitSearchShow(driver);
        if (searchInput == null) {
            severe("Không tìm thấy ô tìm kiếm !");
            return;
        }

        enterSearchKey(searchInput, channelName);

        List<WebElement> elements = driver.findElements(By.id("text"));

        for (int i = 0; i < elements.size(); i++) {
            WebElement ele = elements.get(i);
            if (!ele.getText().isEmpty()) {
                try {
                    WebElement aTag = ele.findElement(By.tagName("a"));
                } catch (NoSuchElementException e) {
//                    info(ele.getText());
                    if (ele.getText().equals(channelName)) {
                        info("Click vào channel");
                        ele.click();
                        CommonUtil.pause(5);
                        break;
                    }
                }
            }
        }

        WebElement scrollContainer = CommonUtil.waitElement(driver, By.id("tabsContent"), null);
        if (scrollContainer != null) {
            List<WebElement> e = scrollContainer.findElements(By.tagName("paper-tab"));
            warning("Size " + e.size());
            int indexVideos = 1;
            for (int i = 0; i < e.size(); i++) {
                warning("Text " + e.get(i).getText());
                if (indexVideos == i) {
                    e.get(i).click();
                    CommonUtil.pause(1);
                    WebElement grid = CommonUtil.waitElement(driver, By.tagName("ytd-grid-renderer"), null);
                    if (grid != null) {
                        WebElement items = grid.findElement(By.id("items"));
                        if (items != null) {
                            List<WebElement> videos = items.findElements(By.tagName("ytd-grid-video-renderer"));
                            warning("Size videos " + videos.size());
                            for (WebElement video : videos) {
                                if (video.getText().isEmpty()) {
                                    continue;
                                }
                                CommonUtil.pause(3);
                                warning(video.getText());
                                if (new Random().nextBoolean()) {
                                    video.click();
                                }
                                // TODO lay video duration để pause ?
                                CommonUtil.pause(20);
                                // Back ve tab videos chuyen sang video khac
                                driver.navigate().back();
                            }
                        } else {
                            warning("Items not found");
                        }
                    } else  {
                        warning("Grid not found");
                    }
                    break;
                }
            }
        }
//        WebElement scrollContainer = CommonUtil.waitElement(driver, By.id("scroll-container"), null);
//        if (scrollContainer != null) {
//            WebElement items = CommonUtil.waitElement(driver, By.id("items"), null);
//            if (items != null) {
//                List<WebElement> videos = items.findElements(By.className("style-scope"));
//                warning("Video number " + videos.size());
//                for (int i = 0; i < videos.size(); i++) {
//                    WebElement video = videos.get(i);
//                    if (!video.getText().isEmpty()) {
//                        info(video.getText());
//                        video.click();
//                        CommonUtil.pause(10);
//                    }
//                }
//            } else {
//                warning("không thấy video trong kênh");
//            }
//        } else {
        // Tìm trên UI khác xem
//            warning("không thấy scroll container trong kênh");
//            WebElement content = CommonUtil.waitElement(driver, By.id("content"), null);
//            if (content != null) {
//                List<WebElement> videos = content.findElements(By.className("style-scope"));
//                info("Video number " + videos.size());
//                for (int i = 0; i < videos.size(); i++) {
//                    WebElement video = videos.get(i);
//                    info(video.getText());
//                }
//            }
//        }

    }

    private void enterSearchKey(WebElement searchInput, String key) {
        info("Nhập từ khoá tìm kiếm : " + key);
        searchInput.click();
        CommonUtil.enterKeys(searchInput, key);
        searchInput.sendKeys(Keys.RETURN);
        CommonUtil.pause(2);
    }
}
