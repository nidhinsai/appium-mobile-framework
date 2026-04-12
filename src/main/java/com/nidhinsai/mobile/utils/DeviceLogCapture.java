package com.nidhinsai.mobile.utils;

import com.nidhinsai.mobile.base.DriverManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.LogEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Captures device-level logs for Android (logcat) and iOS (syslog / XCUITest).
 * Captured logs are written to {@code test-output/device-logs/} for inclusion
 * in failure evidence packages.
 */
public final class DeviceLogCapture {

    private static final Logger LOG = LogManager.getLogger(DeviceLogCapture.class);
    private static final String DEVICE_LOG_DIR = "test-output/device-logs";
    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private DeviceLogCapture() {
    }

    /**
     * Pulls device logs from Appium and writes them to a timestamped file.
     *
     * @param label Descriptive label (test name, scenario name).
     * @return Captured log content as a String, or empty string on failure.
     */
    public static String capture(String label) {
        AppiumDriver driver = DriverManager.getDriver();
        if (driver == null) {
            LOG.warn("Device log capture requested but no AppiumDriver on thread {}",
                    Thread.currentThread().getName());
            return "";
        }
        return capture(driver, label);
    }

    /**
     * Pulls device logs via an explicitly supplied driver.
     */
    public static String capture(AppiumDriver driver, String label) {
        if (driver == null) {
            LOG.warn("Device log capture called with null driver for '{}'", label);
            return "";
        }

        String logType = resolveLogType(driver);
        LOG.debug("Capturing device logs (type={}) for '{}'", logType, label);

        try {
            List<LogEntry> entries = driver.manage().logs().get(logType).getAll();
            if (entries.isEmpty()) {
                LOG.debug("No device log entries found for type '{}'", logType);
                return "";
            }

            StringBuilder sb = new StringBuilder();
            for (LogEntry entry : entries) {
                sb.append(entry.getTimestamp())
                  .append(" [").append(entry.getLevel()).append("] ")
                  .append(entry.getMessage())
                  .append("\n");
            }

            String content = sb.toString();
            writeToFile(label, logType, content);
            return content;

        } catch (Exception e) {
            // Not all Appium configurations expose device logs — degrade gracefully.
            LOG.warn("Could not retrieve device logs (type={}): {}", logType, e.getMessage());
            return "";
        }
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private static String resolveLogType(AppiumDriver driver) {
        if (driver instanceof AndroidDriver) return "logcat";
        if (driver instanceof IOSDriver)     return "syslog";
        return "server";
    }

    private static void writeToFile(String label, String logType, String content) {
        try {
            Path dir  = Paths.get(DEVICE_LOG_DIR);
            Files.createDirectories(dir);

            String safe     = label.replaceAll("[^a-zA-Z0-9._-]", "_").replaceAll("_+", "_");
            String ts       = LocalDateTime.now().format(TIMESTAMP_FMT);
            String fileName = ts + "_" + safe + "_" + logType + ".log";
            Path   file     = dir.resolve(fileName);

            Files.writeString(file, content, StandardCharsets.UTF_8);
            LOG.info("Device log ({}) saved → {}", logType, file.toAbsolutePath());
        } catch (IOException e) {
            LOG.error("Failed to write device log file for '{}': {}", label, e.getMessage());
        }
    }
}
