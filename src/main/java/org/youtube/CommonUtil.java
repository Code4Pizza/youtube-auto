package org.youtube;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.youtube.google.GoogleException;

public class CommonUtil {

	public static void pause(long second) {
		try {
			Thread.sleep(second * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void enterKeys(WebElement input, String key) {
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

	public static By getBy(WebDriver driver, String className) {
		return By.className(className);
	}

	public static WebElement waitElement(WebDriver driver, String className, Map<String, String> mapAttrs) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 10);
			wait.until(ExpectedConditions.elementToBeClickable(By.className(className)));
			return findByClassAndAttrs(driver, className, mapAttrs);
		} catch (RuntimeException e) {
			return null;
		}
	}

	public static WebElement findByClassAndAttrs(WebDriver driver, String className, Map<String, String> mapAttrs) {
		if (mapAttrs == null) {
			try {
				return driver.findElement(By.className(className));
			} catch (NoSuchElementException e) {
				e.printStackTrace();
				return null;
			}
		}

		List<WebElement> elements = driver.findElements(By.className(className));

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
