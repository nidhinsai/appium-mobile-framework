package com.nidhinsai.mobile.tests;

import com.nidhinsai.mobile.base.DriverManager;
import com.nidhinsai.mobile.pages.HomeScreen;
import com.nidhinsai.mobile.pages.LoginScreen;
import com.nidhinsai.mobile.utils.GestureUtil;
import com.nidhinsai.mobile.utils.ScreenshotUtil;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Story("Home Screen")
public class HomeScreenTest extends BaseMobileTest {

    private HomeScreen homeScreen;

    @BeforeClass(alwaysRun = true)
    public void loginToApp() {
        log.info("HomeScreenTest setup: logging in");
        homeScreen = new LoginScreen(DriverManager.getDriver())
                .loginAs("demo.user", "Secret123");
    }

    @Test(description = "Home screen should be fully loaded after login",
            groups = {"home", "smoke"})
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies that the home screen loads correctly after a successful login.")
    public void homeScreenShouldBeLoaded() {
        log.info("TEST: verifying home screen is loaded");
        ScreenshotUtil.capture("home_screen_loaded");

        Assertions.assertThat(homeScreen.isLoaded())
                .as("Home screen should be visible")
                .isTrue();

        log.info("TEST PASS: home screen is loaded");
    }

    @Test(description = "Home screen title should not be blank",
            groups = {"home", "smoke"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that the home screen title text is not empty.")
    public void homeScreenTitleShouldNotBeBlank() {
        log.info("TEST: verifying home screen title is not blank");

        String title = homeScreen.getTitle();
        log.info("Home screen title: '{}'", title);

        Assertions.assertThat(title)
                .as("Home screen title should not be blank")
                .isNotBlank();

        log.info("TEST PASS: home screen title is '{}'", title);
    }

    @Test(description = "Swipe up should scroll the home screen",
            groups = "home")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies that swipe-up gesture does not crash the app.")
    public void swipeUpShouldNotCrashApp() {
        log.info("TEST: swipeUp gesture on home screen");

        GestureUtil.swipeUp(DriverManager.getDriver());
        ScreenshotUtil.capture("after_swipe_up");

        Assertions.assertThat(homeScreen.isLoaded())
                .as("App should remain open after swipe-up")
                .isTrue();

        log.info("TEST PASS: app still running after swipeUp");
    }

    @Test(description = "Logout should return to login screen",
            groups = "home")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that tapping logout returns the user to the login screen.")
    public void logoutShouldReturnToLoginScreen() {
        log.info("TEST: tapping logout button");

        var loginScreen = homeScreen.logout();
        ScreenshotUtil.capture("after_logout");

        Assertions.assertThat(loginScreen)
                .as("logout() should return a LoginScreen object")
                .isNotNull();

        log.info("TEST PASS: returned to login screen after logout");
    }
}
