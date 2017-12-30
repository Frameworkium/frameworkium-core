package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.ui.driver.AbstractDriver;
import io.appium.java_client.windows.WindowsDriver;
import io.appium.java_client.windows.WindowsElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;

import static com.frameworkium.core.common.properties.Property.*;

public class WinAppDriverImpl extends AbstractDriver {

    protected static final Logger logger = LogManager.getLogger();

    @Override
    public Capabilities getCapabilities() {
        logger.info("Creating capabilities for WinAppDriverImpl");
        MutableCapabilities mutableCapabilities = new MutableCapabilities();
        mutableCapabilities.setCapability("platformName", "Windows");
        if (PLATFORM_VERSION.isSpecified()) {
            mutableCapabilities.setCapability(
                    "platform", "Windows " + PLATFORM_VERSION.getValue());
            mutableCapabilities.setCapability(
                    "platformVersion", PLATFORM_VERSION.getValue());
        } else {
            String message = "Platform version needs to be specified when using Windows";
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        if (DEVICE.isSpecified()) {
            mutableCapabilities.setCapability("deviceName", DEVICE.getValue());
        } else {
            mutableCapabilities.setCapability("deviceName", "WindowsPC");
        }

        if (APP_PATH.isSpecified()) {
            mutableCapabilities.setCapability("app", APP_PATH.getValue());
        } else {
            mutableCapabilities.setCapability("app", "Root");
        }

        return mutableCapabilities;
    }

    @Override
    public WebDriver getWebDriver(Capabilities capabilities) {
        return new WindowsDriver<WindowsElement>(capabilities);
    }

}
