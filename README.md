# appium-mobile-framework

Cross-platform mobile automation framework for Android and iOS native apps using Appium 2, Java, TestNG, and Page Object Model.

## Stack

- Java 17
- Appium Java Client
- TestNG
- BrowserStack-ready config pattern
- Android + iOS capability profiles

## Features

- Android and iOS configuration separation
- Local or cloud execution readiness
- Driver manager abstraction
- Reusable screen objects
- TestNG execution flow

## Run

```bash
mvn clean test -Dplatform=android
mvn clean test -Dplatform=ios
```