package com.nidhinsai.mobile.tests;

import com.nidhinsai.mobile.base.DriverManager;
import com.nidhinsai.mobile.pages.HomeScreen;
import com.nidhinsai.mobile.pages.LoginScreen;
import com.nidhinsai.mobile.utils.ScreenshotUtil;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

@Story("Login")
public class LoginTest extends BaseMobileTest {

    @Test(description = "Valid credentials should navigate to the home screen",
            groups = "login")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies that a user with valid credentials successfully reaches the home screen after login.")
    public void shouldLoginWithValidCredentials() {
        log.info("TEST: Login with valid credentials");
        LoginScreen loginScreen = new LoginScreen(DriverManager.getDriver());
        ScreenshotUtil.capture("pre_login");

        HomeScreen homeScreen = loginScreen.loginAs("demo.user", "Secret123");
        ScreenshotUtil.capture("post_login");

        Assertions.assertThat(homeScreen.isLoaded())
                .as("Home screen should be visible after successful login")
                .isTrue();

        log.info("TEST PASS: Home screen is visible after login");
    }

    @Test(description = "Invalid password should show error message",
            groups = "login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that an invalid password shows a suitable error on the login screen.")
    public void shouldShowErrorOnInvalidPassword() {
        log.info("TEST: Login with invalid password");
        LoginScreen loginScreen = new LoginScreen(DriverManager.getDriver());
        ScreenshotUtil.capture("pre_invalid_login");

        LoginScreen afterFailure = loginScreen.loginExpectingFailure("demo.user", "wrong_password");
        ScreenshotUtil.capture("post_invalid_login");

        Assertions.assertThat(afterFailure.hasErrorMessage())
                .as("Error message should be displayed on invalid login")
                .isTrue();

        log.info("TEST PASS: Error displayed for invalid credentials");
    }

    @Test(description = "Empty credentials should show validation error",
            groups = "login")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies that leaving credentials empty shows a validation error.")
    public void shouldShowValidationErrorOnEmptyCredentials() {
        log.info("TEST: Login with empty credentials");
        LoginScreen loginScreen = new LoginScreen(DriverManager.getDriver());

        LoginScreen afterFailure = loginScreen.loginExpectingFailure("", "");
        ScreenshotUtil.capture("empty_credentials");

        Assertions.assertThat(afterFailure.hasErrorMessage())
                .as("Validation error should be shown when credentials are empty")
                .isTrue();

        log.info("TEST PASS: Validation error shown for empty credentials");
    }
}