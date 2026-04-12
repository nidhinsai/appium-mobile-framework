package com.nidhinsai.mobile.tests;

import com.nidhinsai.mobile.base.DriverManager;
import com.nidhinsai.mobile.listeners.MobileTestListener;
import com.nidhinsai.mobile.utils.DeviceLogCapture;
import com.nidhinsai.mobile.utils.ScreenshotUtil;
import java.net.MalformedURLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@Listeners(MobileTestListener.class)
public abstract class BaseMobileTest {

    protected final Logger log = LogManager.getLogger(getClass());

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        log.info("[Setup] Initialising AppiumDriver for {}", getClass().getSimpleName());
        try {
            DriverManager.initDriver();
            log.info("[Setup] AppiumDriver ready on thread {}", Thread.currentThread().getName());
        } catch (MalformedURLException e) {
            // Fail the test explicitly with a clean message instead of letting TestNG
            // bubble up a configuration error that masks the real problem.
            String msg = "Failed to initialise Appium driver — invalid server URL: " + e.getMessage();
            log.error("[Setup] {}", msg, e);
            Assert.fail(msg);
        } catch (Exception e) {
            String msg = "Failed to initialise Appium driver: " + e.getMessage();
            log.error("[Setup] {}", msg, e);
            Assert.fail(msg);
        }
    }

    /**
     * Captures a screenshot + device logs on failure before quitting the driver.
     * Evidence is saved by {@link MobileTestListener}; this method provides an
     * extra inline capture hook at the @AfterMethod level.
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (!result.isSuccess()) {
            String testName = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
            log.error("[TearDown] Test FAILED: {} — capturing inline evidence", testName);
            ScreenshotUtil.capture(testName + "_teardown");
            DeviceLogCapture.capture(testName + "_teardown");
        }
        log.info("[TearDown] Quitting AppiumDriver for thread {}", Thread.currentThread().getName());
        DriverManager.quitDriver();
    }
}