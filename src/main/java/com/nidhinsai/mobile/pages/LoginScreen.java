package com.nidhinsai.mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

/**
 * Screen object for the Login screen.
 * Uses fluent API returning the appropriate next screen.
 */
public class LoginScreen extends BaseScreen {

    private final By usernameField  = AppiumBy.accessibilityId("username");
    private final By passwordField  = AppiumBy.accessibilityId("password");
    private final By loginButton    = AppiumBy.accessibilityId("login_button");
    private final By errorMessage   = AppiumBy.accessibilityId("login_error");

    public LoginScreen(AppiumDriver driver) {
        super(driver);
        log.info("LoginScreen initialised");
    }

    public LoginScreen open() {
        log.info("LoginScreen — screen should be visible on launch");
        return this;
    }

    /**
     * Performs a successful login and returns the HomeScreen.
     */
    public HomeScreen loginAs(String username, String password) {
        log.info("Logging in as '{}'", username);
        type(usernameField, username);
        type(passwordField, password);
        tap(loginButton);
        return new HomeScreen(driver);
    }

    /**
     * Performs a login that is expected to fail (wrong credentials etc.).
     * Stays on the LoginScreen.
     */
    public LoginScreen loginExpectingFailure(String username, String password) {
        log.info("Attempting login with '{}' expecting failure", username);
        type(usernameField, username);
        type(passwordField, password);
        tap(loginButton);
        return this;
    }

    public boolean hasErrorMessage() {
        return isVisible(errorMessage);
    }

    public String getErrorMessage() {
        return text(errorMessage);
    }

    /** @deprecated use {@link #loginAs(String, String)} instead */
    @Deprecated
    public void login(String user, String pass) {
        loginAs(user, pass);
    }
}