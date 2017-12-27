package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.ui.driver.AbstractDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class LegacyFirefoxImpl extends AbstractDriver {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        final DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        // required for legacy firefox
        capabilities.setCapability("marionette", false);
        return capabilities;
    }

    @Override
    public WebDriver getWebDriver(DesiredCapabilities capabilities) {
        return new FirefoxDriver(capabilities);
    }
}
