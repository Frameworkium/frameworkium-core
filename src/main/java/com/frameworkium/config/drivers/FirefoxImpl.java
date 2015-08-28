package com.frameworkium.config.drivers;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class FirefoxImpl extends ProxyableDriver {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        Proxy currentProxy = getProxy();
        if (currentProxy != null) {
            capabilities.setCapability(CapabilityType.PROXY, currentProxy);
        }
        return capabilities;
    }

    @Override
    public WebDriver getWebDriverObject(final DesiredCapabilities capabilities) {
        return new FirefoxDriver(capabilities);
    }

}
