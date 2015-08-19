package com.frameworkium.config.drivers;

import com.frameworkium.config.DriverType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

public class SafariImpl extends DriverType {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        if (isMobile()) {
            return new DesiredCapabilities();
        } else {
            DesiredCapabilities capabilities = DesiredCapabilities.safari();
            capabilities.setCapability("safari.cleanSession", true);
            return capabilities;
        }
    }

    @Override
    public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
        if (isMobile()) {
            throw new IllegalArgumentException(
                    "seleniumGridURL or sauceUser and sauceKey must be specified when running on iOS");
        } else {
            return new SafariDriver(capabilities);
        }
    }

}
