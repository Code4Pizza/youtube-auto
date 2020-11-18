package org.youtube.script;

import org.openqa.selenium.WebDriver;
import org.youtube.SpamViewProc;
import org.youtube.articles.VNExpressScenario;
import org.youtube.entities.YoutubeAccount;
import org.youtube.google.GoogleScenario;
import org.youtube.util.CommonUtil;
import org.youtube.util.DriverUtil;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.youtube.util.LogUtil.info;

public class ReadArticlesScript implements Runnable{

    private final CountDownLatch countDownLatch;
    private final YoutubeAccount account;

    private WebDriver driver;
    private GoogleScenario googleScenario;
    private VNExpressScenario vnExpressScenario;

    public ReadArticlesScript(CountDownLatch countDownLatch, YoutubeAccount account) {
        this.countDownLatch = countDownLatch;
        this.account = account;
    }

    @Override
    public void run() {
        driver = DriverUtil.initChrome(null);
        googleScenario = new GoogleScenario(driver);
        vnExpressScenario = new VNExpressScenario(driver);

        try {
//            if (!account.isFake()) {
//                googleScenario.goGoogleSignInPageThrough3rdParty();
//                googleScenario.attemptToLogin(account);
//            }
            vnExpressScenario.goToHomePage();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            info("==================End of acc " + account.getEmail() + "==================");
            // SpamViewProc.increaseNumberAttempt();
            countDownLatch.countDown();
            CommonUtil.pause(1);
//            driver.quit();
        }
    }
}
