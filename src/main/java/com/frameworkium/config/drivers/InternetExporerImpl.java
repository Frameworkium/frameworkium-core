package com.frameworkium.config.drivers;

import com.frameworkium.config.DriverType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class InternetExporerImpl extends DriverType {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
        capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
        capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true);
        capabilities.setCapability("requireWindowFocus", true);
        return capabilities;
    }

    @Override
    public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
        return new InternetExplorerDriver(capabilities);
    }

    /**
     * IE unfortunately doesn't clearing anything.
     * Browser teardown and re-build is needed
     */
    @Override
    public boolean clearSession(boolean requiresReset) {
        if (requiresReset) {
            tearDownDriver();
            instantiate();
        }
        return true;
    }

}
