package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.ui.driver.Driver;
import com.frameworkium.core.ui.driver.DriverType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

public class SafariImpl extends DriverType {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        if (Driver.isMobile()) {
            return new DesiredCapabilities();
        } else {
            DesiredCapabilities capabilities = DesiredCapabilities.safari();
            capabilities.setCapability("safari.cleanSession", true);
            return capabilities;
        }
    }

    @Override
    public WebDriver getWebDriver(DesiredCapabilities capabilities) {
        if (Driver.isMobile()) {
            throw new IllegalArgumentException(
                    "seleniumGridURL or sauceUser and sauceKey must be specified when running on iOS");
        } else {
            return new SafariDriver(capabilities);
        }
    }

}
