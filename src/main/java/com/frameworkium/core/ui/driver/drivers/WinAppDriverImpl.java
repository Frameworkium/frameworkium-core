package com.frameworkium.core.ui.driver.drivers;

import static com.frameworkium.core.common.properties.Property.APP_PATH;
import static com.frameworkium.core.common.properties.Property.DEVICE;
import static com.frameworkium.core.common.properties.Property.PLATFORM_VERSION;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.frameworkium.core.ui.driver.AbstractDriver;

import io.appium.java_client.windows.WindowsDriver;
import io.appium.java_client.windows.WindowsElement;

public class WinAppDriverImpl extends AbstractDriver {

    protected static final Logger logger = LogManager.getLogger();

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        logger.info("Creating desired capabilities for WinAppDriverImpl");
        final DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("platformName", "Windows");
        if (PLATFORM_VERSION.isSpecified()) {
            desiredCapabilities.setCapability("platform",
                "Windows " + PLATFORM_VERSION.getValue());
            desiredCapabilities.setCapability("platformVersion",
                PLATFORM_VERSION.getValue());
        } else {
            logger.error("Platform version needs to be specified when using Windows!");
        }
        if (DEVICE.isSpecified()) {
            desiredCapabilities.setCapability("deviceName", DEVICE.getValue());
        } else {
            desiredCapabilities.setCapability("deviceName", "WindowsPC");
        }
        if (APP_PATH.isSpecified()) {
            desiredCapabilities.setCapability("app", APP_PATH.getValue());
        } else {
            desiredCapabilities.setCapability("app", "Root");
        }

        return desiredCapabilities;
    }

    @Override
    public WebDriver getWebDriver(DesiredCapabilities capabilities) {
        return new WindowsDriver<WindowsElement>(capabilities);
    }

}
