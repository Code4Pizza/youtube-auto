package org.youtube.movie;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.youtube.util.LogUtil;

public class ZingTVScenario {

    private final WebDriver driver;

    public ZingTVScenario(WebDriver driver) {
        this.driver = driver;
    }

    public void goToHomePage() {
        ((JavascriptExecutor) driver).executeScript("window.open('https://tv.zing.vn/')");
    }

    public void searchMovieName() {
        String name = "Thang nam voi va";

        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(By.className("--z--icon-close")));

        WebElement signInClosePopup = driver.findElement(By.className("--z--icon-close"));
        if (signInClosePopup != null) {
            signInClosePopup.click();
        } else {
            LogUtil.warning("Not found sign In close");
        }

        WebElement webElement = driver.findElement(By.xpath("//*[@id=\"q\"]"));
        if (webElement != null) {
            webElement.sendKeys(name);
            webElement.sendKeys(Keys.RETURN);
        } else {
            LogUtil.warning("Not found search input");
        }
    }

}
