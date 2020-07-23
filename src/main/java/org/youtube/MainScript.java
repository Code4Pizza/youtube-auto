package org.youtube;

import org.openqa.selenium.WebDriver;
import org.youtube.google.GoogleScenario;
import org.youtube.youtube.YouTubeScenario;

import java.util.ArrayList;
import java.util.List;

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
				googleScenario.goToGoogleSignInPage(false);
				googleScenario.attempToLoginGoogle(account);

				for (String url : TEST_URLS) {
					youtubeScenario.openLink(url);
				}

				googleScenario.attempToSignOutGoogle();

				CommonUtil.pause(3);
			} catch (Exception e) {
				continue;
			}
		}
		
		DriverUtil.close();
	}


	public static void main(String[] args) {
		WebDriver driver = DriverUtil.getInstance();
		GoogleScenario googleScenario = new GoogleScenario(driver);
		YouTubeScenario youtubeScenario = new YouTubeScenario(driver);
		MainScript mainScript = new MainScript(googleScenario, youtubeScenario);

		mainScript.playScenario3();
	}
}
