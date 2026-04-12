package com.nidhinsai.mobile.tests;

import com.nidhinsai.mobile.base.DriverManager;
import com.nidhinsai.mobile.pages.LoginScreen;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

public class LoginTest extends BaseMobileTest {

    @Test
    public void shouldLoginToMobileApp() {
        LoginScreen loginScreen = new LoginScreen(DriverManager.getDriver());
        loginScreen.login("demo.user", "Secret123");
        Assertions.assertThat(loginScreen.homeTitle()).isNotBlank();
    }
}