package com.nidhinsai.mobile.base;

import com.nidhinsai.mobile.config.ConfigLoader;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import java.net.MalformedURLException;
import java.net.URL;
import org.openqa.selenium.remote.DesiredCapabilities;

public final class DriverManager {
    private static final ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void initDriver() throws MalformedURLException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        String platform = System.getProperty("platform", "android").toLowerCase();

        capabilities.setCapability("platformName", ConfigLoader.get("platformName", platform));
        capabilities.setCapability("appium:automationName", ConfigLoader.get("automationName", "UiAutomator2"));
        capabilities.setCapability("appium:deviceName", ConfigLoader.get("deviceName", "Emulator"));

        if (platform.equals("ios")) {
            capabilities.setCapability("appium:bundleId", ConfigLoader.get("bundleId", "com.example.ios"));
            DRIVER.set(new IOSDriver(new URL(ConfigLoader.get("serverUrl", "http://127.0.0.1:4723")), capabilities));
        } else {
            capabilities.setCapability("appium:appPackage", ConfigLoader.get("appPackage", "com.example.android"));
            capabilities.setCapability("appium:appActivity", ConfigLoader.get("appActivity", ".MainActivity"));
            DRIVER.set(new AndroidDriver(new URL(ConfigLoader.get("serverUrl", "http://127.0.0.1:4723")), capabilities));
        }
    }

    public static AppiumDriver getDriver() {
        return DRIVER.get();
    }

    public static void quitDriver() {
        if (DRIVER.get() != null) {
            DRIVER.get().quit();
            DRIVER.remove();
        }
    }
}