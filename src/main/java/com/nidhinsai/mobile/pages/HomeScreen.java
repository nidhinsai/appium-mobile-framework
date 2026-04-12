package com.nidhinsai.mobile.pages;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;

/**
 * Page object for the Home / Dashboard screen after successful login.
 */
public class HomeScreen extends BaseScreen {

    // ---------- Locators (XPath / AccessibilityId — adjust to real app) ----------
    private static final By SCREEN_TITLE   = By.xpath("//android.widget.TextView[@content-desc='home_title'] | //XCUIElementTypeStaticText[@name='home_title']");
    private static final By PROFILE_ICON   = By.xpath("//android.widget.ImageView[@content-desc='profile_icon'] | //XCUIElementTypeImage[@name='profile_icon']");
    private static final By NAV_MENU       = By.xpath("//android.widget.LinearLayout[@content-desc='nav_menu'] | //XCUIElementTypeTabBar");
    private static final By LOGOUT_BUTTON  = By.xpath("//android.widget.Button[@content-desc='logout_button'] | //XCUIElementTypeButton[@name='logout_button']");
    private static final By WELCOME_TEXT   = By.xpath("//android.widget.TextView[contains(@text,'Welcome')] | //XCUIElementTypeStaticText[contains(@value,'Welcome')]");

    public HomeScreen(AppiumDriver driver) {
        super(driver);
        log.info("HomeScreen initialised");
    }

    /**
     * Returns true if the home screen's title element is visible.
     */
    public boolean isLoaded() {
        boolean loaded = isVisible(SCREEN_TITLE);
        log.info("HomeScreen.isLoaded() = {}", loaded);
        return loaded;
    }

    /**
     * Returns the main heading text of the home screen.
     */
    public String getTitle() {
        return text(SCREEN_TITLE);
    }

    /**
     * Returns the welcome greeting text (e.g. "Welcome, John").
     */
    public String getWelcomeText() {
        return text(WELCOME_TEXT);
    }

    /**
     * Taps the profile icon in the top bar.
     */
    public HomeScreen tapProfile() {
        log.info("Tapping profile icon");
        tap(PROFILE_ICON);
        return this;
    }

    /**
     * Taps a navigation menu item by visible label text.
     *
     * @param label the exact text label of the tab/menu item
     */
    public HomeScreen navigateTo(String label) {
        log.info("Navigating to section: '{}'", label);
        By navItem = By.xpath(
                String.format("//android.widget.TextView[@text='%s'] | //XCUIElementTypeButton[@name='%s']", label, label));
        tap(navItem);
        return this;
    }

    /**
     * Opens the side/hamburger menu (if present).
     */
    public HomeScreen openNavMenu() {
        log.info("Opening navigation menu");
        tap(NAV_MENU);
        return this;
    }

    /**
     * Taps the logout button and returns the LoginScreen.
     */
    public LoginScreen logout() {
        log.info("Logging out from HomeScreen");
        tap(LOGOUT_BUTTON);
        return new LoginScreen(driver);
    }
}
