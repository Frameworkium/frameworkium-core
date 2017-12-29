package com.frameworkium.integration;

import com.frameworkium.core.ui.driver.AbstractDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Used as a test of CustomImpl functionality
 */
public class CustomFirefoxImpl extends AbstractDriver {

    @Override
    public Capabilities getCapabilities() {
        return new FirefoxOptions();
    }

    @Override
    public WebDriver getWebDriver(Capabilities capabilities) {
        return new FirefoxDriver(new FirefoxOptions(capabilities));
    }

}
