package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.ui.driver.AbstractDriver;
import com.frameworkium.core.ui.driver.Driver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

public class SafariImpl extends AbstractDriver {

    @Override
    public Capabilities getCapabilities() {
        if (Driver.isMobile()) {
            return new SafariOptions();
        } else {
            SafariOptions safariOptions = new SafariOptions();
            safariOptions.setCapability("safari.cleanSession", true);
            return safariOptions;
        }
    }

    @Override
    public WebDriver getWebDriver(Capabilities capabilities) {
        if (Driver.isMobile()) {
            throw new IllegalArgumentException(
                    "seleniumGridURL or sauceUser and sauceKey must be specified when running on iOS");
        } else {
            return new SafariDriver(new SafariOptions(capabilities));
        }
    }

}
