package org.youtube;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.Collections;

public class DriverUtil {

    private static WebDriver driver;

    public static WebDriver getInstance() {
        if (driver == null) {
            driver = initChrome();
        }
        return driver;
    }

    public static WebDriver initChrome() {
        String path = System.getProperty("user.dir");
        System.setProperty("webdriver.chrome.driver", path + "/src/main/resources/chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

//        Proxy proxy = new org.openqa.selenium.Proxy();
//        proxy.setHttpProxy("45.158.186.9:13901");
//        proxy.setSocksUsername("cJwk0y");
//        proxy.setSocksPassword("SouwRE");
//		DesiredCapabilities desiredCapabilities = DesiredCapabilities.chrome();
//		desiredCapabilities.setCapability(CapabilityType.PROXY, proxy);

//		desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, options);

//		return new ChromeDriver(desiredCapabilities);

//        options.setProxy(proxy);

        options.addExtensions(new File("C:\\IdeaProjects\\youtube-auto\\src\\main\\resources\\proxy.zip"));
        return new ChromeDriver(options);
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
        }
    }

}
