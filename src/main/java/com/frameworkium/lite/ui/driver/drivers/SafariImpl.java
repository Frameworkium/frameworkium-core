package com.frameworkium.lite.ui.driver.drivers;

import com.frameworkium.lite.ui.driver.AbstractDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

public class SafariImpl extends AbstractDriver {

    @Override
    public SafariOptions getCapabilities() {
        var safariOptions = new SafariOptions();
        safariOptions.setCapability("safari.cleanSession", true);
        return safariOptions;
    }

    @Override
    public WebDriver getWebDriver(Capabilities capabilities) {
        return new SafariDriver(new SafariOptions(capabilities));
    }

}
