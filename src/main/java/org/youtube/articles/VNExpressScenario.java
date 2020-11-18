package org.youtube.articles;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.youtube.util.CommonUtil;

import java.util.Collections;
import java.util.Map;
import java.util.Random;

import static org.youtube.util.LogUtil.severe;

public class VNExpressScenario {

    private WebDriver driver;

    public VNExpressScenario(WebDriver driver) {
        this.driver = driver;
    }

    public void goToHomePage() {
        driver.get("https://vnexpress.net/");
        Map<String, String> attrs = Collections.singletonMap("data-medium", "Item-1");

        String[] latestIndex = new String[] {"/html/body/section[4]/div/div/div/article/p[1]/a",
                "/html/body/section[4]/div/div/div/div/div/div/ul/li[1]/h3/a",
                "/html/body/section[4]/div/div/div/div/div/div/ul/li[2]/h3/a"};

        WebElement element = driver.findElement(By.xpath(latestIndex[new Random().nextInt(2)]));

        if (element != null) {
            element.click();
        }
//
//        WebElement signInButton = CommonUtil.waitElement(driver, "title_news", attrs);
//        if (signInButton != null) {
//            CommonUtil.pause(1);
//            signInButton.click();
//        }
    }

    public void readLatests() {

    }
}
