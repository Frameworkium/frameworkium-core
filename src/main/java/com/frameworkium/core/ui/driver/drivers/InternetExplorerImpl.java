package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.ui.driver.DriverType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class InternetExplorerImpl extends DriverType {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
        capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
        capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true);
        capabilities.setCapability("requireWindowFocus", true);
        return capabilities;
    }

    @Override
    public WebDriver getWebDriver(DesiredCapabilities capabilities) {
        return new InternetExplorerDriver(capabilities);
    }
}
