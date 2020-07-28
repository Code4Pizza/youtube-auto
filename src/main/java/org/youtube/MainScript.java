package org.youtube;

import com.codahale.metrics.jdbi3.strategies.DefaultNameStrategy;
import com.google.gson.Gson;
import io.dropwizard.jdbi3.JdbiFactory;
import org.jdbi.v3.core.Jdbi;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.youtube.configuration.CircuitBreakerConfiguration;
import org.youtube.entities.YoutubeAccount;
import org.youtube.google.GoogleScenario;
import org.youtube.storage.Accounts;
import org.youtube.storage.FaultTolerantDatabase;
import org.youtube.youtube.YouTubeException.YouTubeFailedToPlayException;
import org.youtube.youtube.YouTubeScenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainScript {

    public static final String TEST_EMAIL = "datvt.aic@gmail.com";
    public static final String TEST_PASSWORD = "Cris123#E";
    public static final String TEST_BACKUP_EMAIL = "";

    public static final String TEST_EMAIL_2 = "Dibdskbskzhsislnxlsnxkcnnzkbx@gmail.com";
    public static final String TEST_PASSWORD_2 = "Thienglocasd3";
    public static final String TEST_BACKUP_EMAIL_2 = "Thienglocasd3@gmail.com";

    public static final String TEST_EMAIL_3 = "Bisywhdyuwokdtvaidkagxkla@gmail.com";
    public static final String TEST_PASSWORD_3 = "Cangthua1";
    public static final String TEST_BACKUP_EMAIL_3 = "Cangthua1@gmail.com";

    @SuppressWarnings("serial")
    public static List<String> TEST_URLS = new ArrayList<String>() {
        {
            add("https://youtu.be/G8WtgWO-yGQ");
            add("https://youtu.be/BYmgWtJmH-I");
        }
    };

    public static List<String> TAMMY = new ArrayList<String>() {
        {
            add("https://www.youtube.com/watch?v=3UcZgaa7aFk");
            add("https://www.youtube.com/watch?v=1aVuVz2OwlQ");
            add("https://www.youtube.com/watch?v=bjP7lcgwHuQ");
            add("https://www.youtube.com/watch?v=lY_pQIXb_qY");
            add("https://www.youtube.com/watch?v=NAieOZt1AlA");
        }
    };

    private GoogleScenario googleScenario;
    private YouTubeScenario youtubeScenario;

    public MainScript(GoogleScenario googleScenario, YouTubeScenario youtubeScenario) {
        this.googleScenario = googleScenario;
        this.youtubeScenario = youtubeScenario;
    }

    public void playScenario1() {
        try {
            googleScenario.goToGoogleSignInPage(true);
            googleScenario.attempToLoginGoogle(new GGAccount(TEST_EMAIL_3, TEST_PASSWORD_3, TEST_BACKUP_EMAIL_3));

            for (String url : TEST_URLS) {
                youtubeScenario.openLink(url);
            }

            googleScenario.attempToSignOutGoogle();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DriverUtil.close();
        }
    }

    public void playScenario2() {
        try {
            youtubeScenario.attempToSearch("Lãng quên chiều thu");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DriverUtil.close();
        }
    }

    public void playScenario3() {
        List<GGAccount> accounts = ExcelUtil.getAccounts();

        for (GGAccount account : accounts) {
            try {
                System.out.println("Start with account : " + account.getEmail());
                googleScenario.goToGoogleSignInPage(false);
                googleScenario.attempToLoginGoogle(account);
                //
                if (adThread != null) {
                    System.out.println("Interupt Ads");
                    adThread.interrupt();
                }
                for (String url : TAMMY) {
                    findingAds();
                    youtubeScenario.openLink(url);
                }

                googleScenario.attempToSignOutGoogle();

                CommonUtil.pause(2);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        System.out.println("End scripts");

//		DriverUtil.close();
    }

    private Thread adThread;

    private Integer totalAds = 0;
    private Integer clickedAds = 0;

    private void findingAds() {
        adThread = new Thread(runable);
        adThread.start();
    }

    private Runnable runable = new Runnable() {

        @Override
        public void run() {
            WebDriver driver = DriverUtil.getInstance();
            System.out.println("Waiting ad show");
            WebElement adButton = CommonUtil.waitAdShow(driver, "ytp-ad-button-text", null);
            if (adButton != null) {
                System.out.println("Found Ads, Check it");
                if (canClick(totalAds, clickedAds)) {
                    CommonUtil.pause(new Random().nextInt(3) + 1);

                    Actions actions = new Actions(driver);

                    actions.moveToElement(adButton).click().perform();

                    CommonUtil.pause(5);

                    Set<String> sets = driver.getWindowHandles();
                    // System.out.println(tab);
                    List<String> lists = new ArrayList<>(sets);

                    if (lists.size() > 1) {
                        clickedAds++;
                        System.out.println("Click ad success " + clickedAds);
                        driver.switchTo().window(lists.get(0));
                        CommonUtil.pause(1);
                        try {
                            youtubeScenario.attempToPlay();
                        } catch (YouTubeFailedToPlayException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } else {
                    System.out.println("Can not click this ads");
                }
                // driver.close();
            } else {
                System.out.println("Time out waiting ads   ");
            }

            CommonUtil.pause(300);

            adThread = new Thread(this);
            adThread.start();

        }
    };

    private boolean canClick(Integer totalAds, Integer clickedAds) {
        if (clickedAds == 0 && totalAds == 0)
            return true;
        else return (float) clickedAds / totalAds < 0.1;
    }

    public static void main(String[] args) {
//        WebDriver driver = DriverUtil.initChrome();
//        GoogleScenario googleScenario = new GoogleScenario(driver);
//        YouTubeScenario youtubeScenario = new YouTubeScenario(driver);
//        MainScript mainScript = new MainScript(googleScenario, youtubeScenario);
//
//        mainScript.playScenario3();


        // VD ve viec su dung thao tac DB
        Jdbi jdbi = Jdbi.create("jdbc:mysql://35.240.231.255:3306/yt_bot?autoReconnect=true&useSSL=false",
                "youtube", "Aic@2020");
        FaultTolerantDatabase accountDatabase = new FaultTolerantDatabase("accounts_database", jdbi, new CircuitBreakerConfiguration());
        Accounts accounts = new Accounts(accountDatabase);
        List<YoutubeAccount> youtubeAccounts = accounts.getAllAccounts();
        youtubeAccounts.forEach(item -> {
            System.out.println(new Gson().toJson(item));
        });


    }
}
