package com.frameworkium.core.ui.browsers;

import org.openqa.selenium.JavascriptExecutor;

public class UserAgent {

    private static final String SCRIPT = "return navigator.userAgent;";

    public static String getUserAgent(JavascriptExecutor driver) {
        try {
            return (String) driver.executeScript(SCRIPT);
        } catch (Exception ignored) {
            return null;
        }
    }
}
