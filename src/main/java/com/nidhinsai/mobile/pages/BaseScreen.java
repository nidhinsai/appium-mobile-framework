package com.nidhinsai.mobile.pages;

import com.nidhinsai.mobile.config.ConfigLoader;
import io.appium.java_client.AppiumDriver;
import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Common base for all screen objects.
 * Provides configurable waits, tap/type helpers, and logging.
 */
public abstract class BaseScreen {

    protected final Logger log = LogManager.getLogger(getClass());
    protected final AppiumDriver driver;
    protected final WebDriverWait wait;
    protected final FluentWait<AppiumDriver> fluentWait;

    protected BaseScreen(AppiumDriver driver) {
        this.driver = driver;
        int explicitSec = Integer.parseInt(ConfigLoader.get("appium.timeout.explicit", "15"));
        int pollingMs   = Integer.parseInt(ConfigLoader.get("appium.timeout.polling_ms", "500"));

        this.wait       = new WebDriverWait(driver, Duration.ofSeconds(explicitSec));
        this.fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(explicitSec))
                .pollingEvery(Duration.ofMillis(pollingMs))
                .ignoring(org.openqa.selenium.NoSuchElementException.class);
    }

    protected void tap(By locator) {
        log.debug("tap: {}", locator);
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String value) {
        log.debug("type '{}' into {}", value, locator);
        var el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        el.clear();
        el.sendKeys(value);
    }

    protected String text(By locator) {
        String t = wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
        log.debug("text({}) = '{}'", locator, t);
        return t;
    }

    protected boolean isVisible(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected void clear(By locator) {
        log.debug("clear: {}", locator);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).clear();
    }

    protected String attribute(By locator, String attr) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getAttribute(attr);
    }
}