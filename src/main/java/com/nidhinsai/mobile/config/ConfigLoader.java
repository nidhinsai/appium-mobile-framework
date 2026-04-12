package com.nidhinsai.mobile.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Thread-safe, immutable config loader for mobile test properties.
 * Loads from classpath first; falls back to file-system for local IDE runs.
 */
public final class ConfigLoader {

    private static final Logger LOG = LogManager.getLogger(ConfigLoader.class);
    private static final Properties PROPERTIES = new Properties();

    static {
        String platform     = System.getProperty("platform", "android").toLowerCase();
        String resourcePath = "config/" + platform + ".properties";

        // 1. Try classpath (jar-safe, CI-safe)
        try (InputStream is = ConfigLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is != null) {
                PROPERTIES.load(is);
                LOG.debug("Loaded mobile config from classpath: {}", resourcePath);
            }
        } catch (IOException e) {
            LOG.warn("IOException loading classpath resource '{}': {}", resourcePath, e.getMessage());
        }

        // 2. Fall back to file-system for local runs
        if (PROPERTIES.isEmpty()) {
            java.nio.file.Path fsPath = Paths.get(resourcePath);
            if (Files.exists(fsPath)) {
                try (InputStream is = Files.newInputStream(fsPath)) {
                    PROPERTIES.load(is);
                    LOG.debug("Loaded mobile config from file-system: {}", fsPath.toAbsolutePath());
                } catch (IOException e) {
                    LOG.warn("IOException loading file-system resource '{}': {}", resourcePath, e.getMessage());
                }
            } else {
                LOG.warn("Mobile config not found at classpath or file-system: {}", resourcePath);
            }
        }

        LOG.info("ConfigLoader ready — platform={} deviceName={}",
                platform, PROPERTIES.getProperty("deviceName", "(not set)"));
    }

    private ConfigLoader() {
    }

    public static String get(String key, String defaultValue) {
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) return override;
        return PROPERTIES.getProperty(key, defaultValue);
    }

    /** Throws if a required key is missing. */
    public static String getRequired(String key) {
        String value = get(key, null);
        if (value == null) {
            throw new IllegalStateException("Required mobile config key missing: '" + key + "'");
        }
        return value;
    }
}