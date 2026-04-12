package com.nidhinsai.mobile.base;

import com.nidhinsai.mobile.config.ConfigLoader;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Thread-safe AppiumDriver factory.
 * Uses typed option classes (UiAutomator2Options / XCUITestOptions) instead of
 * the deprecated DesiredCapabilities, as required by Appium 2+.
 */
public final class DriverManager {

    private static final Logger LOG = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void initDriver() throws MalformedURLException {
        String platform  = System.getProperty("platform", "android").toLowerCase();
        String serverUrl = ConfigLoader.get("serverUrl", "http://127.0.0.1:4723");
        int    implicitWaitSec = Integer.parseInt(ConfigLoader.get("appium.timeout.implicit", "5"));

        LOG.info("Initialising AppiumDriver — platform={} serverUrl={} thread={}",
                platform, serverUrl, Thread.currentThread().getName());

        AppiumDriver driver;

        if ("ios".equals(platform)) {
            XCUITestOptions options = new XCUITestOptions()
                    .setDeviceName(ConfigLoader.get("deviceName", "iPhone Simulator"))
                    .setBundleId(ConfigLoader.get("bundleId", "com.example.ios"));
            String platformVersion = ConfigLoader.get("platformVersion", "");
            if (!platformVersion.isBlank()) {
                options.setPlatformVersion(platformVersion);
            }
            String app = ConfigLoader.get("app", "");
            if (!app.isBlank()) {
                options.setApp(app);
            }
            LOG.debug("iOS caps: bundleId={} device={}",
                    options.getBundleId(), options.getDeviceName());
            driver = new IOSDriver(new URL(serverUrl), options);
        } else {
            UiAutomator2Options options = new UiAutomator2Options()
                    .setDeviceName(ConfigLoader.get("deviceName", "Android Emulator"))
                    .setAppPackage(ConfigLoader.get("appPackage", "com.example.android"))
                    .setAppActivity(ConfigLoader.get("appActivity", ".MainActivity"));
            String platformVersion = ConfigLoader.get("platformVersion", "");
            if (!platformVersion.isBlank()) {
                options.setPlatformVersion(platformVersion);
            }
            String app = ConfigLoader.get("app", "");
            if (!app.isBlank()) {
                options.setApp(app);
            }
            options.setNoReset(Boolean.parseBoolean(ConfigLoader.get("noReset", "false")));
            LOG.debug("Android caps: package={} activity={} device={}",
                    options.getAppPackage(), options.getAppActivity(), options.getDeviceName());
            driver = new AndroidDriver(new URL(serverUrl), options);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWaitSec));
        DRIVER.set(driver);
        LOG.info("AppiumDriver started — sessionId={}", driver.getSessionId());
    }

    public static AppiumDriver getDriver() {
        return DRIVER.get();
    }

    public static void quitDriver() {
        AppiumDriver driver = DRIVER.get();
        if (driver != null) {
            LOG.info("Quitting AppiumDriver — sessionId={} thread={}",
                    driver.getSessionId(), Thread.currentThread().getName());
            driver.quit();
            DRIVER.remove();
        }
    }
}