package com.nidhinsai.mobile.tests;

import com.nidhinsai.mobile.base.DriverManager;
import java.net.MalformedURLException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseMobileTest {
    @BeforeMethod
    public void setUp() throws MalformedURLException {
        DriverManager.initDriver();
    }

    @AfterMethod
    public void tearDown() {
        DriverManager.quitDriver();
    }
}