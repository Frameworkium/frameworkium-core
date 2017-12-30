package com.frameworkium.core.ui.browsers;

import com.frameworkium.core.ui.driver.Driver;
import org.openqa.selenium.JavascriptExecutor;

import java.util.Optional;

public class UserAgent {

    private static final String SCRIPT = "return navigator.userAgent;";

    private String userAgent;

    public UserAgent(JavascriptExecutor driver) {
        userAgent = determineUserAgent(driver);
    }

    private String determineUserAgent(JavascriptExecutor driver) {
        if (!Driver.isNative()) {
            try {
                return (String) driver.executeScript(SCRIPT);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public Optional<String> getUserAgent() {
        return Optional.ofNullable(userAgent);
    }
}
