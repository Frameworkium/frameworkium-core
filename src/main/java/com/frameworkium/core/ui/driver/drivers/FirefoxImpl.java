package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.ui.driver.AbstractDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class FirefoxImpl extends AbstractDriver {

    @Override
    public Capabilities getCapabilities() {
        return new FirefoxOptions();
    }

    /**
     * Get Firefox WebDriver instance with a custom firefox profile and headless execution if the relevant properties
     * have been set.
     * @param capabilities Capabilities of the browser
     * @return WebDriver instance for firefox browser
     */
    @Override
    public WebDriver getWebDriver(Capabilities capabilities) {
        return new FirefoxDriver(new FirefoxOptions(capabilities));
    }
}
