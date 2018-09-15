package com.frameworkium.core.ui.browsers;

import org.openqa.selenium.JavascriptExecutor;

public class UserAgent {

    public static final String SCRIPT = "return navigator.userAgent;";

    private UserAgent() {
        // hidden
    }

    public static String getUserAgent(JavascriptExecutor driver) {
        try {
            return (String) driver.executeScript(SCRIPT);
        } catch (Exception ignored) {
            return null;
        }
    }
}
