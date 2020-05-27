package com.frameworkium.lite.ui.driver.drivers;

import com.frameworkium.lite.ui.driver.AbstractDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;

public class InternetExplorerImpl extends AbstractDriver {

    @Override
    public InternetExplorerOptions getCapabilities() {
        InternetExplorerOptions ieOptions = new InternetExplorerOptions();
        ieOptions.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
        ieOptions.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true);
        ieOptions.setCapability("requireWindowFocus", true);
        return ieOptions;
    }

    @Override
    public WebDriver getWebDriver(Capabilities capabilities) {
        return new InternetExplorerDriver(new InternetExplorerOptions(capabilities));
    }
}
