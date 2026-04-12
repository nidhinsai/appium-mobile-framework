package com.nidhinsai.mobile.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class ConfigLoader {
    private static final Properties PROPERTIES = new Properties();

    static {
        String platform = System.getProperty("platform", "android").toLowerCase();
        try (FileInputStream fis = new FileInputStream("config/" + platform + ".properties")) {
            PROPERTIES.load(fis);
        } catch (IOException ignored) {
        }
    }

    private ConfigLoader() {
    }

    public static String get(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }
}