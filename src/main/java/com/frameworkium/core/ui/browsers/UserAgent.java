package com.frameworkium.core.ui.browsers;

import com.frameworkium.core.ui.driver.Driver;
import org.openqa.selenium.JavascriptExecutor;

import java.util.Optional;

public class UserAgent {

    private static final String SCRIPT = "return navigator.userAgent;";

    private UserAgent() {
        // hide constructor for util class
    }

    public static Optional<String> determineUserAgent(JavascriptExecutor driver) {
        try {
            return Optional.ofNullable(Driver.isNative() ? "" : (String) driver.executeScript(SCRIPT));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
