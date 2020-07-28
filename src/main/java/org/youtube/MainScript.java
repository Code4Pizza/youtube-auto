package org.youtube;

import com.codahale.metrics.jdbi3.strategies.DefaultNameStrategy;
import com.google.gson.Gson;
import io.dropwizard.jdbi3.JdbiFactory;
import org.jdbi.v3.core.Jdbi;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.youtube.configuration.CircuitBreakerConfiguration;
import org.youtube.entities.Banner;
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
            // add("https://www.youtube.com/watch?v=DThWPOzXz0A");
            // add("https://www.youtube.com/watch?v=Y9qIIHZTKW4");
            add("https://www.youtube.com/watch?v=7ie0-OODa-Q");
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

        for (int i = 0; i < accounts.size() - 1; i++) {
            try {
                System.out.println("Banner " + i + " " + accounts.get(i).getEmail());
                googleScenario.goToGoogleSignInPage(false);
                googleScenario.attempToLoginGoogle(accounts.get(i));
                //
                // if (adThread != null) {
                // System.out.println("Interupt Ads");
                // adThread.interrupt();
                // }
                // for (String url : TAMMY) {
                // findingAds();
                // youtubeScenario.openLink(url);
                // }

//				googleScenario.attempToSignOutGoogle();

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

    private int adCount = 0;

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
                System.out.println("Found Ads, Click it");
                CommonUtil.pause(new Random().nextInt(3) + 1);

                Actions actions = new Actions(driver);

                actions.moveToElement(adButton).click().perform();

                // adButton.click();

                CommonUtil.pause(5);

                Set<String> sets = driver.getWindowHandles();
                List<String> lists = new ArrayList<String>();
                for (String tab : sets) {
                    // System.out.println(tab);
                    lists.add(tab);
                }

                if (lists.size() > 1) {
                    adCount++;
                    System.out.println("Click ad success " + adCount);
                    driver.switchTo().window(lists.get(0));
                    CommonUtil.pause(1);
                    try {
                        youtubeScenario.attempToPlay();
                    } catch (YouTubeFailedToPlayException e) {
                        // TODO Auto-generated catch block
                        // e.printStackTrace();
                    }
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

    public static void main(String[] args) {

        String url = "jdbc:mysql://10.240.190.1:3306/cms";
        String user = "aichat";
        String pass = "signalchat@2019";

        Jdbi jdbi = Jdbi.create(url, user, pass);

        FaultTolerantDatabase database = new FaultTolerantDatabase("youtube", jdbi, new CircuitBreakerConfiguration());
        Accounts accounts = new Accounts(database);
        List<Banner> banners = accounts.getBanners(8);
        for (Banner banner : banners){
            System.out.println(new Gson().toJson(banner));
        }

        // for (int i = 0; i < 2; i++) {
//        WebDriver driver = DriverUtil.initChrome();
//        GoogleScenario googleScenario = new GoogleScenario(driver);
//        YouTubeScenario youtubeScenario = new YouTubeScenario(driver);
//        MainScript mainScript = new MainScript(googleScenario, youtubeScenario);
//
//        mainScript.playScenario3();

        // driver.quit();
        // }

        // for( int i = 0; i < 2; i++) {
        // for (String url : TEST_URLS) {
        // try {
        // youtubeScenario.openLink(url);
        // } catch (YouTubeFailedToPlayException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // continue;
        // }
        // }
        // }
    }
}
