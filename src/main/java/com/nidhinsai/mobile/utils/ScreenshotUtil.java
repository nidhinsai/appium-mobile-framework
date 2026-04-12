package com.nidhinsai.mobile.utils;

import com.nidhinsai.mobile.base.DriverManager;
import io.appium.java_client.AppiumDriver;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;

/**
 * Captures screenshots from the Appium session and persists them to disk.
 * Screenshots are stored in {@code test-output/screenshots/} with a
 * timestamp+label filename so they are easy to correlate with test results.
 */
public final class ScreenshotUtil {

    private static final Logger LOG = LogManager.getLogger(ScreenshotUtil.class);
    private static final String SCREENSHOT_DIR = "test-output/screenshots";
    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtil() {
    }

    /**
     * Takes a screenshot via the current thread's AppiumDriver.
     *
     * @param label Descriptive label used as the filename prefix (test name, step description, etc.)
     * @return PNG bytes of the screenshot — also written to disk.
     *         Returns an empty byte array if the driver is unavailable or capture fails.
     */
    public static byte[] capture(String label) {
        AppiumDriver driver = DriverManager.getDriver();
        if (driver == null) {
            LOG.warn("Screenshot requested but no AppiumDriver available for thread {}",
                    Thread.currentThread().getName());
            return new byte[0];
        }
        return capture(driver, label);
    }

    /**
     * Takes a screenshot via an explicitly supplied driver (e.g. in listener callbacks).
     */
    public static byte[] capture(AppiumDriver driver, String label) {
        if (driver == null) {
            LOG.warn("Screenshot requested with null AppiumDriver for label '{}'", label);
            return new byte[0];
        }
        try {
            byte[] png      = driver.getScreenshotAs(OutputType.BYTES);
            String safe     = sanitise(label);
            String ts       = LocalDateTime.now().format(TIMESTAMP_FMT);
            String fileName = ts + "_" + safe + ".png";

            Path dir    = Paths.get(SCREENSHOT_DIR);
            Files.createDirectories(dir);
            Path target = dir.resolve(fileName);
            Files.write(target, png);

            LOG.info("Screenshot saved → {}", target.toAbsolutePath());
            return png;
        } catch (IOException e) {
            LOG.error("Failed to save screenshot for '{}': {}", label, e.getMessage(), e);
            return new byte[0];
        }
    }

    private static String sanitise(String input) {
        if (input == null || input.isBlank()) return "unnamed";
        return input.replaceAll("[^a-zA-Z0-9._-]", "_").replaceAll("_+", "_");
    }
}
