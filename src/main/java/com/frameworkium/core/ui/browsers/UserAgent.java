package com.frameworkium.core.ui.browsers;

import org.openqa.selenium.JavascriptExecutor;

import java.util.Optional;

public class UserAgent {

    private static final String SCRIPT = "return navigator.userAgent;";

    private String userAgent = null;

    public UserAgent(JavascriptExecutor driver) {
        try {
            userAgent = (String) driver.executeScript(SCRIPT);
        } catch (Exception ignored) {
        }
    }

    public Optional<String> getUserAgent() {
        return Optional.ofNullable(userAgent);
    }
}
