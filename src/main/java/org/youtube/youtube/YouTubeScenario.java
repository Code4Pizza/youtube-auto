package org.youtube.youtube;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.youtube.entities.ChannelVideo;
import org.youtube.util.CommonUtil;

import java.util.List;
import java.util.Random;

public class YouTubeScenario {

    private static final Logger logger = LoggerFactory.getLogger(YouTubeScenario.class);


    public static final long DEFAULT_DELAY = 600000;

    private final WebDriver driver;

    public YouTubeScenario(WebDriver driver) {
        this.driver = driver;
    }

    public void go(String url) {
        driver.get(url);
    }

    public void openLink(ChannelVideo video) {
        String url = video.getVideoUrl();
        logger.info("Open url " + url);
        long duration = DEFAULT_DELAY;
        long timeToTakeActions = 0;
        driver.get(url);
        duration = getVideoDuration();

        long startAction = System.currentTimeMillis();
        attempToLike();
        attempToSubscribe();
        timeToTakeActions = System.currentTimeMillis() - startAction;
        logger.info("Time to attemp like and sub " + timeToTakeActions);
        logger.info("Duration is : " + duration);
        try {

//            Thread.sleep(Math.min(DEFAULT_DELAY, (duration - timeToTakeActions)));
            Thread.sleep(video.getDuration() * 900);
            // Fix thời gian video lại cho xem khoảng 5 phút thì switch acc
//			Thread.sleep(200000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
            if (playElement != null) {
                logger.info("Found play element, now need to find play button");
                String titleButtonPlay = playElement.getAttribute("title");
                if (titleButtonPlay.contains("Phát") || titleButtonPlay.contains("Play")) {
                    logger.info("Click play video");
                    playElement.click();
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
                logger.info("Click like");
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
                logger.info("Click subscribe");
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


    public static void printElement(WebElement element) {
        logger.info(String.format("Tag name: %s, text: %s",
                element.getTagName(), element.getText()));
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
//			logger.info("Waiting ad show");
//			CommonUtil.pause(10);
//			WebElement adButton = CommonUtil.waitAdShow(driver, "ytp-ad-button-text", null);
//			if (adButton != null) {
//				logger.info("Found Ads, Click it");
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
////					logger.info(tab);
//					lists.add(tab);
//				}
//
//				if (lists.size() > 1) {
//					adCount++;
//					logger.info("Click ad success " + adCount);
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
//				logger.info("Time out waiting ads   ");
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
