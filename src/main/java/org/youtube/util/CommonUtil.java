package org.youtube.util;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CommonUtil {

    public static void pause(long second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void enterKeys(@Nonnull WebElement input, String key) {
        pause(1);
        input.sendKeys("");
        String[] words = key.split("");
        for (String word : words) {
            input.sendKeys(word);
            try {
                Thread.sleep(new Random().nextInt(200));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void click(@Nonnull WebElement button) {
        pause(1);
        button.click();
    }

    public static By getBy(WebDriver driver, String className) {
        return By.className(className);
    }

//	public static WebElement waitAdShow(WebDriver driver, String className, Map<String, String> mapAttrs) {
//		try {
//			WebDriverWait wait = new WebDriverWait(driver, 900);
//			wait.until(ExpectedConditions.elementToBeClickable(By.className(className)));
//			return findByClassAndAttrs(driver, className, mapAttrs);
//		} catch (RuntimeException e) {
//			return null;
//		}
//	}

    public static WebElement waitElement(WebDriver driver, By by, Map<String, String> mapAttrs) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(ExpectedConditions.elementToBeClickable(by));
            return findByClassAndAttrs(driver, by, mapAttrs);
        } catch (RuntimeException e) {
            return null;
        }
    }

//	public static WebElement waitElement(WebDriver driver, String className, Map<String, String> mapAttrs) {
//		try {
//			WebDriverWait wait = new WebDriverWait(driver, 10);
//			wait.until(ExpectedConditions.elementToBeClickable(By.className(className)));
//			return findByClassAndAttrs(driver, className, mapAttrs);
//		} catch (RuntimeException e) {
//			return null;
//		}
//	}

    public static WebElement findByClassAndAttrs(WebDriver driver, By classNameOrId, Map<String, String> mapAttrs) {
        if (mapAttrs == null) {
            try {
                return driver.findElement(classNameOrId);
            } catch (NoSuchElementException e) {
                e.printStackTrace();
                return null;
            }
        }

        List<WebElement> elements = driver.findElements(classNameOrId);

        for (WebElement e : elements) {
            boolean matchedElement = true;
            for (Map.Entry<String, String> entry : mapAttrs.entrySet()) {
                if (!entry.getValue().equals(e.getAttribute(entry.getKey()))) {
                    matchedElement = false;
                    break;
                }
            }
            if (matchedElement) {
                return e;
            }

        }
        return null;
    }
}
