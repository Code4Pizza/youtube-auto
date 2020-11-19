package org.youtube.script;

import org.openqa.selenium.WebDriver;
import org.youtube.articles.VnExpressScenario;
import org.youtube.entities.YoutubeAccount;
import org.youtube.google.GoogleScenario;
import org.youtube.movie.ZingTVScenario;
import org.youtube.util.CommonUtil;
import org.youtube.util.DriverUtil;

import java.util.concurrent.CountDownLatch;

import static org.youtube.util.LogUtil.info;

public class ReadArticlesScript implements Runnable {

    private final CountDownLatch countDownLatch;
    private final YoutubeAccount account;

    private WebDriver driver;
    private GoogleScenario googleScenario;
    private VnExpressScenario vnExpressScenario;
    private ZingTVScenario zingTVScenario;

    public ReadArticlesScript(CountDownLatch countDownLatch, YoutubeAccount account) {
        this.countDownLatch = countDownLatch;
        this.account = account;
    }

    @Override
    public void run() {
        driver = DriverUtil.initChrome(null);
        googleScenario = new GoogleScenario(driver);
        vnExpressScenario = new VnExpressScenario(driver);
        zingTVScenario = new ZingTVScenario(driver);

        try {
//            if (!account.isFake()) {
//                googleScenario.goGoogleSignInPageThrough3rdParty();
//                googleScenario.attemptToLogin(account);
//            }
            vnExpressScenario.runDefaultScript();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            info("==================End of acc " + account.getEmail() + "==================");
            // SpamViewProc.increaseNumberAttempt();
            countDownLatch.countDown();
            CommonUtil.pause(3);
//            driver.quit();
        }
    }
}
