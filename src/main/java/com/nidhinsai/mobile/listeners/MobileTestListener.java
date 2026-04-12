package com.nidhinsai.mobile.listeners;

import com.nidhinsai.mobile.base.DriverManager;
import com.nidhinsai.mobile.utils.DeviceLogCapture;
import com.nidhinsai.mobile.utils.ScreenshotUtil;
import io.appium.java_client.AppiumDriver;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener wired into every test class via {@code @Listeners}.
 *
 * On failure it:
 *   1. Takes a screenshot and saves it to {@code test-output/screenshots/}.
 *   2. Pulls device logs (logcat / syslog) to {@code test-output/device-logs/}.
 *   3. Writes a consolidated failure report to {@code test-output/mobile-failures/}.
 */
public class MobileTestListener implements ITestListener {

    private static final Logger LOG = LogManager.getLogger(MobileTestListener.class);
    private static final String FAILURE_DIR = "test-output/mobile-failures";
    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    @Override
    public void onTestStart(ITestResult result) {
        LOG.info("▶ TEST START: {}.{}",
                result.getTestClass().getName(), result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("✔ TEST PASS : {}.{} ({} ms)",
                result.getTestClass().getName(), result.getMethod().getMethodName(),
                result.getEndMillis() - result.getStartMillis());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName  = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
        long   durationMs = result.getEndMillis() - result.getStartMillis();

        LOG.error("✘ TEST FAIL : {} ({} ms)", testName, durationMs);

        Throwable cause = result.getThrowable();
        if (cause != null) {
            LOG.error("  Exception : {}", cause.getMessage());
            LOG.debug("  Stack trace:", cause);
        }

        AppiumDriver driver = DriverManager.getDriver();

        // 1. Screenshot
        byte[] png = ScreenshotUtil.capture(driver, testName);
        if (png.length == 0) {
            LOG.warn("  No screenshot captured for failed test: {}", testName);
        }

        // 2. Device logs
        String deviceLogs = DeviceLogCapture.capture(driver, testName);

        // 3. Failure report
        writeFailureReport(testName, cause, deviceLogs);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOG.warn("⊘ TEST SKIP : {}.{}",
                result.getTestClass().getName(), result.getMethod().getMethodName());
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private void writeFailureReport(String testName, Throwable cause, String deviceLogs) {
        try {
            Path   dir      = Paths.get(FAILURE_DIR);
            Files.createDirectories(dir);

            String ts       = LocalDateTime.now().format(TIMESTAMP_FMT);
            String safeName = testName.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path   file     = dir.resolve(ts + "_" + safeName + ".txt");

            StringBuilder report = new StringBuilder();
            report.append("TEST      : ").append(testName).append("\n");
            report.append("TIMESTAMP : ").append(LocalDateTime.now()).append("\n");

            if (cause != null) {
                report.append("EXCEPTION : ").append(cause.getMessage()).append("\n\n");
                report.append("STACK TRACE:\n");
                for (StackTraceElement el : cause.getStackTrace()) {
                    report.append("  at ").append(el).append("\n");
                }
            }

            if (deviceLogs != null && !deviceLogs.isBlank()) {
                report.append("\n─── DEVICE LOGS ───\n").append(deviceLogs);
            }

            Files.writeString(file, report.toString(), StandardCharsets.UTF_8);
            LOG.info("  Failure report written → {}", file.toAbsolutePath());
        } catch (IOException e) {
            LOG.error("  Could not write mobile failure report: {}", e.getMessage());
        }
    }
}
