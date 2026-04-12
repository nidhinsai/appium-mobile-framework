package com.nidhinsai.mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;

public class LoginScreen extends BaseScreen {
    private final By username = AppiumBy.accessibilityId("username");
    private final By password = AppiumBy.accessibilityId("password");
    private final By loginButton = AppiumBy.accessibilityId("login_button");
    private final By homeTitle = AppiumBy.accessibilityId("home_title");

    public LoginScreen(AppiumDriver driver) {
        super(driver);
    }

    public void login(String user, String pass) {
        type(username, user);
        type(password, pass);
        tap(loginButton);
    }

    public String homeTitle() {
        return text(homeTitle);
    }
}