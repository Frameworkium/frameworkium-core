package com.frameworkium.config.drivers;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

public class SafariImpl extends ProxyableDriver {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        if (isMobile()) {
            return new DesiredCapabilities();
        } else {
            DesiredCapabilities capabilities = DesiredCapabilities.safari();
            Proxy currentProxy = getProxy();
            if (currentProxy != null) {
                capabilities.setCapability(CapabilityType.PROXY, currentProxy);
            }
            capabilities.setCapability("safari.cleanSession", true);
            return capabilities;
        }
    }

    @Override
    public WebDriver getWebDriverObject(final DesiredCapabilities capabilities) {
        if (isMobile()) {
            throw new IllegalArgumentException(
                    "seleniumGridURL or sauceUser and sauceKey must be specified when running on iOS");
        } else {
            return new SafariDriver(capabilities);
        }
    }

}
