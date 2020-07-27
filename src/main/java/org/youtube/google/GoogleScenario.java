package org.youtube.google;

import org.apache.commons.collections4.map.HashedMap;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.youtube.CommonUtil;
import org.youtube.CookiesUtil;
import org.youtube.GGAccount;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static java.util.Map.entry;

import java.util.Collection;
import java.util.Collections;

public class GoogleScenario {

	public static final String GOOGLE_SIGN_IN_3RD_PARTY = "https://stackoverflow.com/users/login?ssrc=head&returnurl=https%3a%2f%2fstackoverflow.com%2f";

	private WebDriver driver;

	public GoogleScenario(WebDriver driver) {
		this.driver = driver;
	}

	public void goToGoogleSignInPage(boolean use3rdParty) throws GoogleException.GoogleSignInNotFoundException {
		System.out.println("Attemp login");
		if (use3rdParty) {
			driver.get(GOOGLE_SIGN_IN_3RD_PARTY);
			By googleButton = By.className("s-btn__google");
			WebDriverWait wait = new WebDriverWait(driver, 5);
			wait.until(ExpectedConditions.elementToBeClickable(googleButton));
			if (driver.findElement(googleButton) != null) {
				CommonUtil.pause(1);
				driver.findElement(googleButton).click();
			} else {
				throw new GoogleException.GoogleSignInNotFoundException();
			}
		} else {
			driver.get("https://www.google.com/");
			WebElement signInButton = driver.findElement(By.id("gb_70"));
			if (signInButton != null) {
				CommonUtil.pause(1);
				signInButton.click();
			} else {
				throw new GoogleException.GoogleSignInNotFoundException();
			}
		}
	}

	public void attempToLoginGoogle(GGAccount account)
			throws GoogleException.GoogleInputLoginNotFoundException, GoogleException.GoogleSignInNotFoundException {
		// Waiting page finish animation
		CommonUtil.pause(2);

		By ip = By.className("whsOnd");
		By next = By.className("VfPpkd-RLmnJb");

		WebElement input = null;
		WebElement nextButton = null;

		try {
			WebDriverWait wait = new WebDriverWait(driver, 5);
			wait.until(ExpectedConditions.elementToBeClickable(ip));
			wait.until(ExpectedConditions.elementToBeClickable(next));
			input = driver.findElement(ip);
			nextButton = driver.findElement(next);
		} catch (RuntimeException e) {
			System.out.println("Moved to account chooser panel");
			// Moved to account chooser panel
			// Needed to click "Using another account button"
			List<WebElement> tags = driver.findElements(By.className("lCoei"));
			for (WebElement f : tags) {
				if (f.getAttribute("jsname") != null && "rwl3qc".equals(f.getAttribute("jsname"))) {
					System.out.println("Found button switch account");
					f.click();
					// Waiting page finish animation
					CommonUtil.pause(2);
					break;
				}
			}

			input = driver.findElement(ip);
			nextButton = driver.findElement(next);
		}

		if (nextButton == null || input == null) {
			throw new GoogleException.GoogleInputLoginNotFoundException();
		}

		if ("username".equals(input.getAttribute("autocomplete"))) {
			CommonUtil.pause(2);
			CommonUtil.enterKeys(input, account.getEmail());
			nextButton.click();
		}

		// Waiting page finish animation
		CommonUtil.pause(2);

		try {
			nextButton = driver.findElement(By.className("VfPpkd-RLmnJb"));
			input = driver.findElement(By.className("whsOnd"));
		} catch (NoSuchElementException e) {
			// Meet message "This browser or app may not be secure"
			// Fallback to 3rd party sign in
			goToGoogleSignInPage(true);
			attempToLoginGoogle(account);
			return;
		}

		if (nextButton == null || input == null) {
			throw new GoogleException.GoogleInputLoginNotFoundException();
		}

		if ("current-password".equals(input.getAttribute("autocomplete"))) {
			CommonUtil.pause(2);
			CommonUtil.enterKeys(input, account.getPassword());
			nextButton.click();
		}

		CommonUtil.pause(2);

		// Check if account need backup email confirmation
		By buttons = By.className("vxx8jf");
		List<WebElement> elements = driver.findElements(buttons);
		for (WebElement e : elements) {
			if (e.getText() != null && e.getText().contains("Xác nhận")) {
				System.out.println("Found confirm backup");
				e.click();
				CommonUtil.pause(2);

				WebElement backupElement = driver.findElement(By.id("knowledge-preregistered-email-response"));
//				backupElement.sendKeys(account.getBackup());
				CommonUtil.enterKeys(backupElement, account.getBackup());
				By byNext = By.className("VfPpkd-RLmnJb");
				driver.findElement(byNext).click();

				CommonUtil.pause(2);

				By bySkip = By.className("snByac");
				List<WebElement> skipElements = driver.findElements(bySkip);

				for (WebElement s : skipElements) {
					if ("Để sau".equals(s.getText()) || "ĐỂ SAU".equals(s.getText())) {
						System.out.println("Found skip button");
						s.click();
						break;
					}
				}
				break;
			}
		}

		CommonUtil.pause(2);
		System.out.println("Writting cookies");
		CookiesUtil.writeCookies(driver);
	}

	public void attempToSignOutGoogle() throws GoogleException.GoogleSignOutNotFoundException {
		// Need some cool down for syncing google user sign in
		CommonUtil.pause(5);

		driver.get("https://www.google.com/");

		try {
			By avatarButton = By.className("gb_Ua");
			WebDriverWait wait = new WebDriverWait(driver, 5);
			wait.until(ExpectedConditions.elementToBeClickable(avatarButton));
			driver.findElement(avatarButton).click();

			By signOutButton = By.id("gb_71");
			WebDriverWait waitSignOut = new WebDriverWait(driver, 5);
			waitSignOut.until(ExpectedConditions.elementToBeClickable(signOutButton));
			driver.findElement(signOutButton).click();

		} catch (RuntimeException e) {
			throw new GoogleException.GoogleSignOutNotFoundException();
		}
	}

	public void checkReviewActivity() {
		WebElement reviewButton = CommonUtil.findByClassAndAttrs(driver, "gb_zd",
				Map.ofEntries(entry("role", "button"), entry("tabindex", "0")));

		CommonUtil.pause(1);
		if (reviewButton == null) {
			System.out.println("Not found review activity button");
			return;
		}
		reviewButton.click();

		WebElement notificationButton = CommonUtil.waitElement(driver, "PfHrIe",
				Map.ofEntries(entry("jsname", "cDqwkb")));
		CommonUtil.pause(1);
		if (notificationButton == null) {
			System.out.println("Not found notification button");
			return;
		}
		notificationButton.click();

		WebElement confirmButton = CommonUtil.waitElement(driver, "VfPpkd-LgbsSe",
				Map.ofEntries(entry("jsname", "j6LnYe")));
		CommonUtil.pause(1);
		if (confirmButton == null) {
			System.out.println("Not found confirm button");
			return;
		}
		confirmButton.click();
	}

	public void attempToSignOutYouTube() throws GoogleException.GoogleSignOutNotFoundException {
		CommonUtil.pause(10);

		driver.get("https://www.youtube.com/");
		try {
			By avatarButton = By.id("avatar-btn");
			WebDriverWait wait = new WebDriverWait(driver, 5);
			wait.until(ExpectedConditions.elementToBeClickable(avatarButton));
			driver.findElement(avatarButton).click();
		} catch (RuntimeException e) {
			throw new GoogleException.GoogleSignOutNotFoundException();
		}

		CommonUtil.pause(2);

		// Expand drop-down menu from avatar button then find signout item
		System.out.println("Finding signout button");
		List<WebElement> eles = driver.findElements(By.id("endpoint"));

		boolean isFound = false;
		System.out.println(eles.size());
		for (WebElement e : eles) {
			if (e.getAttribute("href") != null && e.getAttribute("href").contains("logout")) {
				isFound = true;
				e.click();
				break;
			}
		}
		if (!isFound) {
			throw new GoogleException.GoogleSignOutNotFoundException();
		}
	}
}
