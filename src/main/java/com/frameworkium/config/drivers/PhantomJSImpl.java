package com.frameworkium.config.drivers;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class PhantomJSImpl extends ProxyableDriver {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
        capabilities.setCapability("takesScreenshot", true);
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS,
                new String[] { "--webdriver-loglevel=NONE" });
        Proxy currentProxy = getProxy();
        if (currentProxy != null) {
            capabilities.setCapability(CapabilityType.PROXY, currentProxy);
        }
        return capabilities;
    }

    @Override
    public WebDriver getWebDriverObject(final DesiredCapabilities capabilities) {
        return new PhantomJSDriver(capabilities);
    }

}
