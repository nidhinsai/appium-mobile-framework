package com.nidhinsai.mobile.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries flaky mobile tests up to MAX_RETRY times before marking them as failed.
 * Default retry count is 1. Override via -Dtest.retry.count=N.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOG = LogManager.getLogger(RetryAnalyzer.class);
    private static final int MAX_RETRY = Integer.parseInt(
            System.getProperty("test.retry.count", "1"));

    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY) {
            retryCount++;
            LOG.warn("Retrying mobile test '{}' — attempt {}/{}",
                    result.getName(), retryCount, MAX_RETRY);
            return true;
        }
        LOG.error("Mobile test '{}' failed after {} retries", result.getName(), MAX_RETRY);
        return false;
    }
}
