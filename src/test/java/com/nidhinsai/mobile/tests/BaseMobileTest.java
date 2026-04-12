package com.nidhinsai.mobile.tests;

import com.nidhinsai.mobile.base.DriverManager;
import com.nidhinsai.mobile.listeners.MobileTestListener;
import com.nidhinsai.mobile.utils.DeviceLogCapture;
import com.nidhinsai.mobile.utils.ScreenshotUtil;
import java.net.MalformedURLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@Listeners(MobileTestListener.class)
public abstract class BaseMobileTest {

    protected final Logger log = LogManager.getLogger(getClass());

    @BeforeMethod
    public void setUp() throws MalformedURLException {
        log.info("[Setup] Initialising AppiumDriver for {}", getClass().getSimpleName());
        DriverManager.initDriver();
        log.info("[Setup] AppiumDriver ready on thread {}", Thread.currentThread().getName());
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