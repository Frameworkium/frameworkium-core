package com.frameworkium.core.ui.browsers;

import com.frameworkium.core.ui.driver.Driver;
import org.openqa.selenium.JavascriptExecutor;

import java.util.Optional;

public class UserAgent {

    private static final String SCRIPT = "return navigator.userAgent;";

    private String userAgent;

    public UserAgent(JavascriptExecutor driver) {
        if (Driver.isNative()) {
            userAgent = null;
        } else {
            try {
                userAgent = (String) driver.executeScript(SCRIPT);
            } catch (Exception ignored) {
            }
        }
    }

    public Optional<String> getUserAgent() {
        return Optional.ofNullable(userAgent);
    }
}
