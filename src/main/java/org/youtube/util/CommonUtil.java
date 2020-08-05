package org.youtube.util;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.annotation.Nonnull;
import java.util.HashMap;
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

	public static WebElement waitAdShow(WebDriver driver, String className, Map<String, String> mapAttrs) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions.elementToBeClickable(By.className(className)));
			return findByClassAndAttrs(driver, getBy(driver, className), mapAttrs);
		} catch (RuntimeException e) {
			return null;
		}
	}

    public static WebElement waitSearchShow(WebDriver driver) {
        try {
            By by = By.id("search");
            WebDriverWait wait = new WebDriverWait(driver, 8);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
            Map<String, String> attrs = new HashMap<>();
            attrs.put("name", "search_query");
            return findByClassAndAttrs(driver, by, attrs);
        } catch (RuntimeException e) {
            return null;
        }
    }

    public static WebElement waitElement(WebDriver driver, By by, Map<String, String> mapAttrs) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 8);
            wait.until(ExpectedConditions.elementToBeClickable(by));
            return findByClassAndAttrs(driver, by, mapAttrs);
        } catch (RuntimeException e) {
            return null;
        }
    }

	public static WebElement waitElement(WebDriver driver, String className, Map<String, String> mapAttrs) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 8);
			wait.until(ExpectedConditions.elementToBeClickable(By.className(className)));
			return findByClassAndAttrs(driver, getBy(driver, className), mapAttrs);
		} catch (RuntimeException e) {
			return null;
		}
	}

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


    /**
     * scrollDown() method scrolls down the page.
     *
     * @return void
     */
    public static void scrollDown(WebDriver driver) {
        try {
            int i = 0;
            for (; i <= 30; i++) {
                ((JavascriptExecutor) driver).executeScript(("window.scrollBy(0," + i + ")"), "");
            }
            for (; i > 0; i--) {
                ((JavascriptExecutor) driver).executeScript(("window.scrollBy(0," + i + ")"), "");
            }
        } catch (WebDriverException wde) {
        } catch (Exception e) {
        }
    }

    /**
     * scrollUp() method scrolls up the page.
     *
     * @return void
     */
    public static void scrollUp(WebDriver driver) {
        try {
            int i = 0;
            for (; i > -20; i--) {
                ((JavascriptExecutor) driver).executeScript(("window.scrollBy(0," + i + ")"), "");
            }
            for (; i < 0; i++) {
                ((JavascriptExecutor) driver).executeScript(("window.scrollBy(0," + i + ")"), "");
            }
        } catch (WebDriverException wde) {
        } catch (Exception e) {
        }
    }

}
