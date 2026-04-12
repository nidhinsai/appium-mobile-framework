package com.nidhinsai.mobile.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

/**
 * Gesture utilities — swipe, scroll, long-press — compatible with Appium 2 / W3C actions.
 */
public final class GestureUtil {

    private static final Logger LOG = LogManager.getLogger(GestureUtil.class);

    private GestureUtil() {
    }

    // ---------- Core swipe helpers ----------

    /**
     * Swipe from a start (x,y) to an end (x,y) in {@code durationMs} milliseconds.
     */
    public static void swipe(AppiumDriver driver, int startX, int startY, int endX, int endY, int durationMs) {
        LOG.debug("swipe ({},{}) → ({},{}) in {}ms", startX, startY, endX, endY, durationMs);
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(durationMs), PointerInput.Origin.viewport(), endX, endY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(java.util.List.of(swipe));
    }

    /**
     * Swipe up (towards top of screen) from the centre — useful for scrolling lists down.
     */
    public static void swipeUp(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int x      = size.width  / 2;
        int startY = (int) (size.height * 0.75);
        int endY   = (int) (size.height * 0.25);
        LOG.info("swipeUp — x={} {} → {}", x, startY, endY);
        swipe(driver, x, startY, x, endY, 600);
    }

    /**
     * Swipe down (towards bottom of screen) from the centre.
     */
    public static void swipeDown(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int x      = size.width  / 2;
        int startY = (int) (size.height * 0.25);
        int endY   = (int) (size.height * 0.75);
        LOG.info("swipeDown — x={} {} → {}", x, startY, endY);
        swipe(driver, x, startY, x, endY, 600);
    }

    /**
     * Swipe left (useful for carousels / side menus).
     */
    public static void swipeLeft(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int y      = size.height / 2;
        int startX = (int) (size.width * 0.80);
        int endX   = (int) (size.width * 0.20);
        LOG.info("swipeLeft — y={} {} → {}", y, startX, endX);
        swipe(driver, startX, y, endX, y, 500);
    }

    /**
     * Swipe right.
     */
    public static void swipeRight(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int y      = size.height / 2;
        int startX = (int) (size.width * 0.20);
        int endX   = (int) (size.width * 0.80);
        LOG.info("swipeRight — y={} {} → {}", y, startX, endX);
        swipe(driver, startX, y, endX, y, 500);
    }

    /**
     * Swipe up repeatedly until {@code locator} becomes visible (max {@code maxSwipes} attempts).
     */
    public static void swipeTo(AppiumDriver driver, By locator, int maxSwipes) {
        LOG.info("swipeTo {} (max {} swipes)", locator, maxSwipes);
        for (int i = 0; i < maxSwipes; i++) {
            try {
                if (!driver.findElements(locator).isEmpty() && driver.findElement(locator).isDisplayed()) {
                    LOG.debug("Element found after {} swipe(s)", i);
                    return;
                }
            } catch (Exception ignored) {
            }
            swipeUp(driver);
        }
        LOG.warn("Element {} still not visible after {} swipes", locator, maxSwipes);
    }

    /**
     * Convenience overload — default 5 swipes.
     */
    public static void swipeTo(AppiumDriver driver, By locator) {
        swipeTo(driver, locator, 5);
    }

    // ---------- Long press ----------

    /**
     * Long-press on the element at {@code locator} for {@code durationMs} milliseconds.
     */
    public static void longPress(AppiumDriver driver, By locator, int durationMs) {
        LOG.info("longPress {} for {}ms", locator, durationMs);
        var element = driver.findElement(locator);
        var rect    = element.getRect();
        int cx = rect.x + rect.width  / 2;
        int cy = rect.y + rect.height / 2;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence lp = new Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), cx, cy))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(durationMs), PointerInput.Origin.viewport(), cx, cy))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(java.util.List.of(lp));
    }

    /**
     * Convenience overload — default 1 500 ms long press.
     */
    public static void longPress(AppiumDriver driver, By locator) {
        longPress(driver, locator, 1500);
    }

    // ---------- Android UiScrollable scroll-to-text ----------

    /**
     * Uses the Android UiScrollable strategy to scroll until a text label is visible.
     * Only works on Android; on iOS, use {@link #swipeTo(AppiumDriver, By)}.
     */
    public static void scrollToText(AppiumDriver driver, String text) {
        if (!(driver instanceof AndroidDriver)) {
            LOG.warn("scrollToText is Android-only; falling back to swipeTo for text: '{}'", text);
            swipeTo(driver, By.xpath(String.format("//*[@text='%s' or @value='%s']", text, text)));
            return;
        }
        LOG.info("scrollToText: '{}'", text);
        Map<String, Object> args = new HashMap<>();
        args.put("strategy", "-android uiautomator");
        args.put("selector",
                "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text(\"" + text + "\"))");
        ((JavascriptExecutor) driver).executeScript("mobile: scroll", args);
    }
}
