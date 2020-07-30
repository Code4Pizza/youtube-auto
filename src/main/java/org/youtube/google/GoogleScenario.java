package org.youtube.google;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.youtube.entities.YoutubeAccount;
import org.youtube.util.CommonUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.youtube.util.Constants.DEFAULT_DELAY_SECOND;
import static org.youtube.util.LogUtil.*;

public class GoogleScenario {

    public static final String GOOGLE_URL = "https://www.google.com/";
    public static final String GOOGLE_SIGN_IN_3RD_PARTY = "https://stackoverflow.com/users/login?ssrc=head&returnurl=https%3a%2f%2fstackoverflow.com%2f";

    private final WebDriver driver;

    public GoogleScenario(WebDriver driver) {
        this.driver = driver;
    }

    public void goGoogleSignInPage() throws GoogleException {
        info("Go to google page");
        driver.get(GOOGLE_URL);

        WebElement signInButton = CommonUtil.waitElement(driver, By.id("gb_70"), null);
        if (signInButton != null) {
            CommonUtil.pause(1);
            signInButton.click();
        } else {
            severe("Button sign in not found, try to sign out");
            attemptSignOut();
        }
    }

    public void goGoogleSignInPageThrough3rdParty() throws GoogleException.GoogleSignInNotFoundException {
        info("Signing in google through 3rd party");
        driver.get(GOOGLE_SIGN_IN_3RD_PARTY);
        By googleButton = By.className("s-btn__google");
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_DELAY_SECOND);
        wait.until(ExpectedConditions.elementToBeClickable(googleButton));
        if (driver.findElement(googleButton) != null) {
            CommonUtil.pause(1);
            driver.findElement(googleButton).click();
        } else {
            throw new GoogleException.GoogleSignInNotFoundException();
        }
    }

    public void attemptToLogin(YoutubeAccount account) throws GoogleException {
        info("Signing in email " + account.getEmail() + " ...");

        // Waiting page finish animation
        CommonUtil.pause(2);

        By ip = By.className("whsOnd");
        By next = By.className("VfPpkd-RLmnJb");

        WebElement input;
        WebElement nextButton;

        try {
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_DELAY_SECOND);
            wait.until(ExpectedConditions.elementToBeClickable(ip));
            wait.until(ExpectedConditions.elementToBeClickable(next));
            input = driver.findElement(ip);
            nextButton = driver.findElement(next);
        } catch (RuntimeException e) {
            warning("Multiple account chooser showing");

            Map<String, String> attrs = Collections.singletonMap("jsname", "rwl3qc");
            WebElement eSwitchAccount = CommonUtil.waitElement(driver, By.className("lCoei"), attrs);
            if (eSwitchAccount != null) {
                info("Click button using another account");
                CommonUtil.click(eSwitchAccount);
                // Waiting page finish animation
                CommonUtil.pause(2);
            } else {
                throw new GoogleException("Button using another account not found");
            }
            // Try to find input again
            input = driver.findElement(ip);
            nextButton = driver.findElement(next);
        }

        if (nextButton == null || input == null) {
            throw new GoogleException.GoogleInputLoginNotFoundException();
        }

        if ("username".equals(input.getAttribute("autocomplete"))) {
            CommonUtil.enterKeys(input, account.getEmail());
            nextButton.click();
        } else {
            severe("Input email not found");
            throw new GoogleException.GoogleInputLoginNotFoundException();
        }

        // Waiting page finish animation
        CommonUtil.pause(2);

        nextButton = CommonUtil.waitElement(driver, By.className("VfPpkd-RLmnJb"), null);
        input = CommonUtil.waitElement(driver, By.className("whsOnd"), null);
//        try {
//            nextButton = driver.findElement(By.className("VfPpkd-RLmnJb"));
//            input = driver.findElement(By.className("whsOnd"));
//        } catch (NoSuchElementException e) {
//            // Meet message "This browser or app may not be secure"
//            // Fallback to 3rd party sign in if proxy provide access domain 3rd party or skip
//            throw new GoogleException("This browser or app may not be secure");
//        }

        if (nextButton == null || input == null) {
            throw new GoogleException.GoogleDetectInsecureBrowserException();
        }

        if ("current-password".equals(input.getAttribute("autocomplete"))) {
            CommonUtil.enterKeys(input, account.getPassword());
            nextButton.click();
        } else {
            severe("Input password not found");
            throw new GoogleException.GoogleInputLoginNotFoundException();
        }

        CommonUtil.pause(2);

        // Check if account need backup email confirmation
        WebElement eConfirmBackup = CommonUtil.waitElement(driver, By.className("vxx8jf"), null);
        if (eConfirmBackup != null && !eConfirmBackup.getText().isEmpty()) {
            info("Google showing confirm backup email panel");
            info("button is :" + eConfirmBackup.getText());
            eConfirmBackup.click();
        } else {
            // Có case đăng nhập qua third party thì k cần p ấn nút confirm, nó tự động nhảy vào input luôn
        }

        // Tìm input, nếu vẫn k có thì 90% tài khoản này xác nhận email back up r, thông báo đăng nhập thành công
        WebElement eBackupInput = CommonUtil.waitElement(driver, By.id("knowledge-preregistered-email-response"), null);
        if (eBackupInput != null) {
            info("eBackupInput is : " + eBackupInput.getText());
            CommonUtil.enterKeys(eBackupInput, account.getBackupEmail());

            WebElement eNextStep = CommonUtil.waitElement(driver, By.className("VfPpkd-RLmnJb"), null);
            if (eNextStep != null) {
                eNextStep.click();

                CommonUtil.pause(5);
                WebElement eSkipButton = CommonUtil.waitElement(driver, By.className("snByac"), null);
                if (eSkipButton != null) {
                    eSkipButton.click();
                } else {
                    throw new GoogleException("Entered backup email, skip button not found");
                }
            } else {
                // Nếu không có thì có khả năng đăng nhập qua 3rd party, nó k hỏi mà tự nhảy về trang chủ 3rd pary luôn
            }

        }
        info("Account " + account.getEmail() + " successfully signed in");

//		System.out.println("Writing cookies");
//		CookiesUtil.writeCookies(driver);
    }

    public void attemptSignOut() throws GoogleException {
        // Need some cool down for syncing google user sign in
        CommonUtil.pause(3);

        info("Signing out...");
        driver.navigate().to(GOOGLE_URL);

        WebElement eAvatar = CommonUtil.waitElement(driver, By.className("gb_Ua"), null);
        if (eAvatar != null) {
            CommonUtil.click(eAvatar);

            WebElement eSignOutButton = CommonUtil.waitElement(driver, By.id("gb_71"), null);
            if (eSignOutButton != null) {
                CommonUtil.click(eSignOutButton);
            } else {
                throw new GoogleException.GoogleSignOutNotFoundException();
            }
        } else {
            throw new GoogleException("Sign out, avatar button not found");
        }
//        try {
//            By avatarButton = By.className("gb_Ua");
//            WebDriverWait wait = new WebDriverWait(driver, 5);
//            wait.until(ExpectedConditions.elementToBeClickable(avatarButton));
//            driver.findElement(avatarButton).click();
//
//            By signOutButton = By.id("gb_71");
//            WebDriverWait waitSignOut = new WebDriverWait(driver, 5);
//            waitSignOut.until(ExpectedConditions.elementToBeClickable(signOutButton));
//            driver.findElement(signOutButton).click();
//
//        } catch (RuntimeException e) {
//            throw new GoogleException.GoogleSignOutNotFoundException();
//        }
    }

    public void checkReviewActivity() {
//        Map<String, String> attrs = new HashMap<>();
//        attrs.put("role", "button");
//        attrs.put("tabindex", "0");
//        WebElement reviewButton = CommonUtil.findByClassAndAttrs(driver, "gb_zd", attrs);
//
//        CommonUtil.pause(1);
//        if (reviewButton == null) {
//            System.out.println("Not found review activity button");
//            return;
//        }
//        reviewButton.click();
//
//        attrs = Collections.singletonMap("jsname", "cDqwkb");
//        WebElement notificationButton = CommonUtil.waitElement(driver, "PfHrIe", attrs);
//
//        CommonUtil.pause(1);
//        if (notificationButton == null) {
//            System.out.println("Not found notification button");
//            return;
//        }
//        notificationButton.click();
//
//        attrs = Collections.singletonMap("jsname", "j6LnYe");
//        WebElement confirmButton = CommonUtil.waitElement(driver, "VfPpkd-LgbsSe", attrs);
//
//        CommonUtil.pause(1);
//        if (confirmButton == null) {
//            System.out.println("Not found confirm button");
//            return;
//        }
//        confirmButton.click();
    }

    public void attemptToSignOutYouTube() throws GoogleException.GoogleSignOutNotFoundException {
        CommonUtil.pause(10);

//        driver.get("https://www.youtube.com/");
        try {
            By avatarButton = By.id("avatar-btn");
            WebDriverWait wait = new WebDriverWait(driver, 5);
            wait.until(ExpectedConditions.elementToBeClickable(avatarButton));
            driver.findElement(avatarButton).click();
        } catch (RuntimeException e) {
            throw new GoogleException.GoogleSignOutNotFoundException();
        }

        CommonUtil.pause(2);

        // Expand drop-down menu from avatar button then find signout item
        System.out.println("Finding signout button");
        List<WebElement> eles = driver.findElements(By.id("endpoint"));

        boolean isFound = false;
        System.out.println(eles.size());
        for (WebElement e : eles) {
            if (e.getAttribute("href") != null && e.getAttribute("href").contains("logout")) {
                isFound = true;
                e.click();
                break;
            }
        }
        if (!isFound) {
            throw new GoogleException.GoogleSignOutNotFoundException();
        }
    }
}
