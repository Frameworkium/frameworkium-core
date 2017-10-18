package com.frameworkium.integration;

import com.frameworkium.core.ui.driver.AbstractDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class CustomFirefoxImpl extends AbstractDriver {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        return DesiredCapabilities.firefox();
    }

    @Override
    public WebDriver getWebDriver(DesiredCapabilities capabilities) {
        return new FirefoxDriver(capabilities);
    }

}
