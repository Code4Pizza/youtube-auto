package org.youtube;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DriverUtil {

	private static WebDriver driver;
	
	private static String USER_AGENT;
	private static String BROWSER;

	
	public static WebDriver getInstance() {
		if (driver == null) {
			driver = initChrome();
		}
		return driver;
	}

	public static WebDriver initChrome() {
		String path = System.getProperty("user.dir");
		System.setProperty("webdriver.chrome.driver", path +"/src/main/resources/chromedriverOS");

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-blink-features");
		options.addArguments("--disable-blink-features=AutomationControlled");

		
//		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
		options.setExperimentalOption("useAutomationExtension", false);

		Map<String, String> mapUA = UserAgentUtil.getRandomUserAgent();
		
		BROWSER =  mapUA.keySet().stream().findFirst().get();
		USER_AGENT = mapUA.get(BROWSER);
		System.out.println(USER_AGENT);
		
//		options.addArguments(String.format("--user-agent=%s", USER_AGENT));
//		options.addArguments("--start-maximized");
		
//		Proxy proxy = new org.openqa.selenium.Proxy();
//		proxy.setHttpProxy("45.158.186.9:13901");
//		proxy.setSocksUsername("cJwk0y");
//		proxy.setSocksPassword("SouwRE");
//		DesiredCapabilities desiredCapabilities = DesiredCapabilities.chrome();
//		desiredCapabilities.setCapability(CapabilityType.PROXY, proxy);
//
//		desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, options);

//		ChromeDriver driver = new ChromeDriver(desiredCapabilities);
		ChromeDriver driver = new ChromeDriver(options);
		return driver;
	}

	public static WebDriver initFirefox() {
		String path = System.getProperty("user.dir");
		System.setProperty("webdriver.gecko.driver",  path +"/resources/chromedriver");
		return new FirefoxDriver();
	}

	public static void close() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {

		}
		if (driver != null) {
			driver.close();
			driver.quit();
		}
	}

}
