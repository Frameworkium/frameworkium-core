package com.frameworkium.config.drivers;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class InternetExporerImpl extends ProxyableDriver {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
        capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
        capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true);
        capabilities.setCapability("requireWindowFocus", true);
        Proxy currentProxy = getProxy();
        if (currentProxy != null) {
            capabilities.setCapability(CapabilityType.PROXY, currentProxy);
        }
        return capabilities;
    }

    @Override
    public WebDriver getWebDriverObject(final DesiredCapabilities capabilities) {
        return new InternetExplorerDriver(capabilities);
    }

}
