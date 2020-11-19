package org.youtube.articles;

import com.google.common.annotations.VisibleForTesting;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.youtube.util.CommonUtil;
import org.youtube.util.LogUtil;

import java.util.Random;

import static org.youtube.util.CommonUtil.Decision.NO;

public class VnExpressScenario extends BaseArticleScenario {

    public static final int MAX_ARTICLE_READ_PER_SESSION = 10;

    private final WebDriver driver;

    public VnExpressScenario(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    String getHomeUrl() {
        return "https://vnexpress.net/";
    }

    @Override
    String[] getTopArticleViewPosition() {
        return new String[]{"/html/body/section[4]/div/div/div/article/h3/a",
                "/html/body/section[4]/div/div/div/div/div/div/ul/li[1]/h3/a",
                "/html/body/section[4]/div/div/div/div/div/div/ul/li[2]/h3/a",
                "/html/body/section[5]/div/div[1]/article[3]/h3/a",
                "/html/body/section[4]/div/div/div/div/div/div/ul/li[3]/article/h3/a",
                "/html/body/section[5]/div/div[1]/article[1]/h3/a",
                "/html/body/section[5]/div/div[3]/div[1]/div/article[1]/div[2]/h3/a",
                "/html/body/section[5]/div/div[3]/div[3]/div/article[1]/div[2]/h3/a"};
    }

    @Override
    void visitHomePage() {
        driver.get(getHomeUrl());
        CommonUtil.pauseRandom(5);
    }

    @Override
    @VisibleForTesting
    void visitRandomArticle() {
        driver.get("https://vnexpress.net/doanh-nghiep-my-tai-trung-quoc-ung-ho-biden-4195462.html");
        CommonUtil.pauseRandom(3);
        readArticle();
    }

    @Override
    void clickLogoToHomePage() {
        WebElement element = CommonUtil.waitElement(driver, By.className("logo"));
        CommonUtil.pauseRandom(2);
        LogUtil.info("Quay về trang chủ");
        if (element != null) {
            element.click();
        } else {
            driver.get(getHomeUrl());
        }
    }

    @Override
    void clickTopArticle() {
        String randomArticle = getTopArticleViewPosition()[new Random().nextInt(getTopArticleViewPosition().length)];
        int maxAttempt = 0;
        while (!mainArticleClicked.contains(randomArticle)) {
            randomArticle = getTopArticleViewPosition()[new Random().nextInt(getTopArticleViewPosition().length)];
            maxAttempt++;
            if (maxAttempt == 20) {
                break;
            }
        }
        WebElement element = CommonUtil.waitElement(driver, By.xpath(randomArticle));
        assert element != null;
        String titleArticle = element.getAttribute("title");
        if (titleArticle != null) {
            LogUtil.info("Read Top article title : " + titleArticle);
            articleRead.add(titleArticle);
        }
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        CommonUtil.pauseRandom(2);
        readArticle();
    }

    @Override
    void clickMostViewArticle() throws Exception {
        LogUtil.info("See most viewed article");
        WebElement element = CommonUtil.waitElement(driver, By.xpath("//*[@id=\"box_topview_detail\"]/article[1]/div[2]/a"));
        if (element == null) {
            throw new Exception("Not found click most view element");
        }
        clickDetailArticle(element);
    }

    @Override
    void clickRelatedTopicArticle() throws Exception {
        LogUtil.info("See related topic article");
        WebElement element = CommonUtil.waitElement(driver, By.xpath("/html/body/section[4]/div/div[2]/article/ul/li[1]/a"));
        if (element == null) {
            throw new Exception("Not found click related topic element");
        }
        clickDetailArticle(element);
    }

    @Override
    void clickRelatedCategoryArticle() throws Exception {
        LogUtil.info("See related category article");
        WebElement element = CommonUtil.waitElement(driver, By.xpath("//*[@id=\"detail_cungChuyenMuc\"]/div/article[1]/h4/a"));
        if (element == null) {
            throw new Exception("Not found click related topic element");
        }
        clickDetailArticle(element);
    }

    private void clickDetailArticle(WebElement element) {
        String titleArticle = element.getAttribute("title");
        if (titleArticle != null) {
            // LogUtil.info("Read article title : " + titleArticle);
            articleRead.add(titleArticle);
        }
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        CommonUtil.pauseRandom(3);
        readArticle();
    }

    @Override
    void readArticle() {
        // get height of article
        WebElement detail = CommonUtil.waitElement(driver, By.className("fck_detail"));
        if (detail != null) {
            int heightArticle = detail.getSize().getHeight();
            // LogUtil.info("Height article " + heightArticle);
            int pixelPerScrolled = 450;
            // LogUtil.info("Number scroll to end article " + heightArticle / pixelPerScrolled);
            for (int i = 0; i < heightArticle / pixelPerScrolled; i++) {
                CommonUtil.scrollDownArticle(driver);
                CommonUtil.pauseRandom(1);
            }
        } else {
            // Có thể là bài viết video
            LogUtil.warning("Not found height");
            // loop min default scroll time
            // for (int i = 0; i < 3; i++) {
            //    CommonUtil.scrollDownArticle(driver);
            //    CommonUtil.pauseRandom(5);
            // }
        }
        LogUtil.info("Finish read article");
        readComments();
        decideNextArticle();
    }

    @Override
    void readComments() {
        if (CommonUtil.randomChoice().equals(NO)) {
            LogUtil.warning("Not a drama day");
            return;
        }
        // Reach to comment section
        CommonUtil.scrollDownArticle(driver);
        CommonUtil.pause(1);
        CommonUtil.scrollDownArticle(driver);
        // read comments
        WebElement comments = CommonUtil.waitElement(driver, By.xpath("//*[@id=\"box_comment_vne\"]/div"));
        if (comments != null) {
            int heightBox = comments.getSize().getHeight();
            LogUtil.info("Height comments " + heightBox);
            WebElement readMore = CommonUtil.waitElement(driver, By.xpath("//*[@id=\"box_comment_vne\"]/div/div[7]/a"));
            if (readMore != null) {
                CommonUtil.pauseRandom(2);
                readMore.click();
                int newHeight = comments.getSize().getHeight() - heightBox;
                LogUtil.info("Expand Height comments " + newHeight);
                int pixelPerScrolled = 450;
                for (int i = 0; i <= newHeight / pixelPerScrolled; i++) {
                    CommonUtil.scrollDownArticle(driver);
                    CommonUtil.pauseRandom(5);
                }
                LogUtil.warning("Finish read comments");
            }
        }
    }

    @Override
    void decideNextArticle() {
        if (numberArticleRead == MAX_ARTICLE_READ_PER_SESSION) {
            onFinishReading();
            return;
        }
        numberArticleRead++;
        long currentTime = System.currentTimeMillis();
        try {
            if (currentTime % 2 == 0) {
                CommonUtil.scrollDown(driver);
                clickRelatedCategoryArticle();
            } else if (currentTime % 3 == 0) {
                CommonUtil.scrollUp(driver);
                clickMostViewArticle();
            } else {
                CommonUtil.scrollDown(driver);
                clickRelatedTopicArticle();
            }
        } catch (Exception e) {
            // e.printStackTrace();
            clickLogoToHomePage();
            CommonUtil.pauseRandom(5);
            clickTopArticle();
        }

    }

    @Override
    void onFinishReading() {
        LogUtil.warning("======================");
        LogUtil.info("Articles read: " + articleRead.size());
        for (String titleRead : articleRead) {
            LogUtil.info(titleRead);
        }
        LogUtil.warning("======================");
    }
}
