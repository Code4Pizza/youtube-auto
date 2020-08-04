package org.youtube.script;

import org.openqa.selenium.WebDriver;
import org.youtube.SpamViewProc;
import org.youtube.entities.ChannelVideo;
import org.youtube.entities.YoutubeAccount;
import org.youtube.google.GoogleScenario;
import org.youtube.util.CommonUtil;
import org.youtube.util.DriverUtil;
import org.youtube.util.ProxyUtil;
import org.youtube.youtube.YouTubeScenario;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.youtube.util.LogUtil.info;
import static org.youtube.util.LogUtil.warning;

public class SearchVideoScript implements Runnable {

    public static final String AD_THREAD = "AD_THREAD";

    private final CountDownLatch countDownLatch;
    private final YoutubeAccount account;
    private final List<ChannelVideo> videos;

    private WebDriver driver;
    private GoogleScenario googleScenario;
    private YouTubeScenario youTubeScenario;

    public SearchVideoScript(CountDownLatch countDownLatch, YoutubeAccount account, List<ChannelVideo> videos,
                             boolean isSpamView, boolean needEthernet) {
        this.countDownLatch = countDownLatch;
        this.account = account;
        this.videos = videos;
    }

    @Override
    public void run() {
        String proxyName = ProxyUtil.findProxyForAccount(!account.isFake());
        driver = DriverUtil.initChrome(null);
        googleScenario = new GoogleScenario(driver);
        youTubeScenario = new YouTubeScenario(driver);

        try {
            info("==================Start of acc " + account.getEmail() + "==================");
            if (!account.isFake()) {
                googleScenario.goGoogleSignInPageThrough3rdParty();
                googleScenario.attemptToLogin(account);
            }
            // TODO running finding Ads
            for (ChannelVideo video : videos) {
                // TODO lưu từ khoá search và tên kênh
                youTubeScenario.attemptToSearch("Heart and soul", "Howie");
                CommonUtil.pause(3);
            }

        } catch (Exception e) {
            e.printStackTrace();
            warning(e.getMessage());
            warning("Skip account " + account.getEmail());
            SpamViewProc.increaseFailedAccount();
        } finally {
            info("==================End of acc " + account.getEmail() + "==================");
            SpamViewProc.increaseNumberAttempt();
            // TODO stop finding Ads
            countDownLatch.countDown();
            CommonUtil.pause(1);
//            driver.quit();
        }
    }
}
