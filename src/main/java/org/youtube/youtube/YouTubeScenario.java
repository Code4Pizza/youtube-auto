package org.youtube.youtube;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.youtube.CommonUtil;
import org.youtube.youtube.YouTubeException.YouTubeFailedToPlayException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class YouTubeScenario {

	public static final long DEFAULT_DELAY = 5000;

	private WebDriver driver;

	public YouTubeScenario(WebDriver driver) {
		this.driver = driver;
	}

	public void openLink(String url) throws YouTubeException.YouTubeFailedToPlayException {
//		CommonUtil.pause(5);

//		findingAds();

		System.out.println("Open url " + url);
		long duration = DEFAULT_DELAY;
		long timeToTakeActions = 0;
		driver.get(url);
		attempToPlay();
		duration = getVideoDuration();

		long startAction = System.currentTimeMillis();
		attempToLike();
		attempToSubscribe();
		timeToTakeActions = System.currentTimeMillis() - startAction;
		System.out.println("Time to attemp like and sub " + timeToTakeActions);

		try {
//			Thread.sleep(duration - timeToTakeActions);
			
			// Fix thời gian video lại cho xem khoảng 5 phút thì switch acc 
			Thread.sleep(200000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} finally {
//			if (adThread != null) {
//				adThread.interrupt();
//			}
		}
	}

	public void attempToPlay() throws YouTubeException.YouTubeFailedToPlayException {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 5);
			wait.until(ExpectedConditions.elementToBeClickable(By.className("ytp-play-button")));
			WebElement playElement = driver.findElement(By.className("ytp-play-button"));
			String titleButtonPlay = playElement.getAttribute("title");
			if (titleButtonPlay.contains("Phát") || titleButtonPlay.contains("Play")) {
				System.out.println("Click play video");
				playElement.click();
			} else {
				System.out.println("Cant find play buton");
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new YouTubeException.YouTubeFailedToPlayException();
		}
	}

	private void attempToLike() {
		CommonUtil.pause(5);
		List<WebElement> elements = driver.findElements(By.xpath("//*[@id=\"button\"]"));
		for (WebElement e : elements) {
			String ariaLabel = e.getAttribute("aria-label");
			String ariaPressed = e.getAttribute("aria-pressed");
			if (ariaLabel == null || ariaPressed == null) {
				continue;
			}
			if (ariaLabel.contains("khác thích video này") && ariaPressed.equals("false")) {
				System.out.println("Click like");
				e.click();
				break;
			}
		}
	}

	private void attempToSubscribe() {
		CommonUtil.pause(5);
		List<WebElement> subElements = driver.findElements(By.className("ytd-subscribe-button-renderer"));
		for (WebElement e : subElements) {
			if ("ĐĂNG KÝ".equals(e.getText())) {
				System.out.println("Click subscribe");
				e.click();
				break;
			}
		}
	}

	private long getVideoDuration() {
		try {
			String time = driver.findElement(By.className("ytp-time-duration")).getText();
			// mm:ss
			String[] units = time.split(":");
			int minutes = Integer.parseInt(units[0]);
			int seconds = Integer.parseInt(units[1]);
			int offset = 2;
			return (60 * minutes + seconds + offset) * 1000;
		} catch (Exception e) {
			// Unknown duration
			return DEFAULT_DELAY;
		}
	}

	public void attempToSearch(String key) {
		try {
			driver.get("https://www.youtube.com/");

			By search = By.id("search");
			WebDriverWait wait = new WebDriverWait(driver, 5);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(search));

			List<WebElement> searchInputs = driver.findElements(search);
			WebElement searchInput = null;

			for (WebElement element : searchInputs) {
				if ("search_query".equals(element.getAttribute("name"))) {
					searchInput = element;
					break;
				}
			}

			if (searchInput == null) {
				return;
			}

			String[] words = key.split(" ");
			for (String word : words) {
				searchInput.sendKeys(word);
				searchInput.sendKeys(" ");
				try {
					Thread.sleep((long) ((500 * new Random().nextFloat()) + 500));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			searchInput.sendKeys(Keys.RETURN);

		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

//	boolean isAdShowing = false;
//
//	private Thread adThread;
//
//	private int adCount = 0;

//	private void findingAds() {
//		adThread = new Thread(runable);
//		adThread.start();
//	}
//
//	private Runnable runable = new Runnable() {
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			System.out.println("Waiting ad show");
//			CommonUtil.pause(10);
//			WebElement adButton = CommonUtil.waitAdShow(driver, "ytp-ad-button-text", null);
//			if (adButton != null) {
//				System.out.println("Found Ads, Click it");
//				CommonUtil.pause(new Random().nextInt(3) + 1);
//
//				Actions actions = new Actions(driver);
//
//				actions.moveToElement(adButton).click().perform();
//
////				adButton.click();
//
//				CommonUtil.pause(5);
//
//				Set<String> sets = driver.getWindowHandles();
//				List<String> lists = new ArrayList<String>();
//				for (String tab : sets) {
////					System.out.println(tab);
//					lists.add(tab);
//				}
//
//				if (lists.size() > 1) {
//					adCount++;
//					System.out.println("Click ad success " + adCount);
//					driver.switchTo().window(lists.get(0));
//					CommonUtil.pause(1);
//					try {
//						attempToPlay();
//					} catch (YouTubeFailedToPlayException e) {
//						// TODO Auto-generated catch block
////						e.printStackTrace();
//					}
//				}
////				driver.close();
//			} else {
//				System.out.println("Time out waiting ads   ");
//			}
//
//			
//			CommonUtil.pause(30);
//			
//			adThread = new Thread(this);
//			adThread.start();
//
//		}
//	};
}
