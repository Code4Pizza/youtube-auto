package org.youtube.util;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class DriverUtil {

    private static WebDriver driver;

    public static ImmutablePair<String, String> userAgent;

    public static WebDriver getInstance() {
        if (driver == null) {
            driver = initChrome();
        }
        return driver;
    }

    public static WebDriver initChrome() {
        // Setup path driver
        String path = System.getProperty("user.dir");
        String os = OSUtil.getOSPrefix();
        System.setProperty("webdriver.chrome.driver", path + "/src/main/resources/chromedriver_" + os);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features");
        options.addArguments("--disable-blink-features=AutomationControlled");
//        options.addArguments("chrome.switches", "--disable-extensions");
//        options.addArguments("user-data-dir=" + "/Users/theanh/Library/Application Support/Google/Chrome");

//		ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        Map<String, String> mapUA = UserAgentUtil.getRandomUserAgent();

        Optional<String> first = mapUA.keySet().stream().findFirst();
        if (first.isPresent()) {
            String browser = first.get();
            userAgent = new ImmutablePair<>(browser, "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/534.18 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/534.18");
        }

//		options.addArguments(String.format("--user-agent=%s", userAgent));
//		options.addArguments("--start-maximized");

		Proxy proxy = new org.openqa.selenium.Proxy();
		proxy.setHttpProxy("45.158.186.9:13901");
		proxy.setSocksUsername("cJwk0y");
		proxy.setSocksPassword("SouwRE");
		DesiredCapabilities desiredCapabilities = DesiredCapabilities.chrome();
		desiredCapabilities.setCapability(CapabilityType.PROXY, proxy);

		desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, options);

		ChromeDriver driver = new ChromeDriver(desiredCapabilities);
//        options.addExtensions(new File(path + "/src/main/resources/proxy.zip"));
//        ChromeDriver driver = new ChromeDriver(options);
//		CookiesUtil.readCookies(driver);
        return driver;
    }

    public static WebDriver initFirefox() {
        String path = System.getProperty("user.dir");
        System.setProperty("webdriver.gecko.driver", path + "/resources/chromedriver");
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
