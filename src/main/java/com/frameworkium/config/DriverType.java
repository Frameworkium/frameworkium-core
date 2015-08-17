package com.frameworkium.config;

import com.frameworkium.capture.ScreenshotCapture;
import com.frameworkium.listeners.CaptureListener;
import com.frameworkium.listeners.EventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import static com.frameworkium.config.SystemProperty.*;
import static com.frameworkium.config.SystemProperty.BROWSER_VERSION;

public abstract class DriverType {

    protected final static Logger logger = LogManager.getLogger(DriverType.class);

    /**
     * Creates the Wrapped Driver object, and returns to the test
     *
     * @return - Wrapped WebDriver object
     */
    public WebDriverWrapper instantiate() {
        logger.info("Current Browser Selection: " + this);

        DesiredCapabilities caps = getDesiredCapabilities();
        logger.info("Caps: " + caps.toString());

        WebDriverWrapper eventFiringWD = new WebDriverWrapper(getWebDriverObject(caps));
        eventFiringWD.register(new EventListener());
        if (ScreenshotCapture.isRequired()) {
            eventFiringWD.register(new CaptureListener());
        }
        return eventFiringWD;
    }

    /**
     * Determines whether tests have been ran to configure against a mobile
     *
     * @return - Boolean, whether using mobile or not
     */
    public static boolean isMobile() {
        return "ios".equalsIgnoreCase(PLATFORM.getValue()) || "android".equalsIgnoreCase(PLATFORM.getValue());
    }

    /**
     * Determines whether a native app is being used for testing
     *
     * @return - Boolean, to whether an native app is defined
     */
    public static boolean isNative() {
        return APP_PATH.isSpecified();
    }

    /**
     * Used by the drivers which are remote, as common desired capabilities
     *
     * @param desiredCapabilities - The browser desired capabilities
     * @return - The browser desired capabilities
     */
    protected static DesiredCapabilities setGeneralRemoteCapabilities(DesiredCapabilities desiredCapabilities) {
        if (PLATFORM.isSpecified()) {
            desiredCapabilities.setPlatform(Platform.valueOf(PLATFORM.getValue().toUpperCase()));
        }
        if (BROWSER_VERSION.isSpecified()) {
            desiredCapabilities.setVersion(BROWSER_VERSION.getValue());
        }
        return desiredCapabilities;
    }

    /**
     * Implemented in each Driver Type to specify the capabilities of that browser
     *
     * @return - Desired Capabilities of each browser
     */
    public abstract DesiredCapabilities getDesiredCapabilities();

    /**
     * Returns the correct WebDriver object for that driver type
     *
     * @param capabilities - Capabilities of the browser
     * @return - WebDriver object for the browser
     */
    public abstract WebDriver getWebDriverObject(DesiredCapabilities capabilities);

}
