package org.youtube.util;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.youtube.entities.YoutubeAccount;
import org.youtube.storage.YoutubeDatabases;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class DriverUtil {


    public static ImmutablePair<String, String> userAgent;

    public static WebDriver initChrome(String proxyName) {
        // Setup path driver
        String path = System.getProperty("user.dir");
        String os = OSUtil.getOSPrefix();
        System.setProperty("webdriver.chrome.driver", path + "/src/main/resources/chromedriver_" + os);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        Map<String, String> mapUA = UserAgentUtil.getRandomUserAgent();

        Optional<String> first = mapUA.keySet().stream().findFirst();
        if (first.isPresent()) {
            String browser = first.get();
            userAgent = new ImmutablePair<>(browser, "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/534.18 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/534.18");
        }


        // tu thong tin account, kiem tra xem co the dung duoc proxy nao bay gio

        String relativePath = "/src/main/resources/" + proxyName;
        String fullPath = path + relativePath + ".zip";
        LogUtil.warning("Full path proxy" + fullPath);
        options.addExtensions(new File(fullPath));
        ChromeDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        return driver;
    }

    public static WebDriver initFirefox() {
        String path = System.getProperty("user.dir");
        System.setProperty("webdriver.gecko.driver", path + "/resources/chromedriver");
        return new FirefoxDriver();
    }
}
