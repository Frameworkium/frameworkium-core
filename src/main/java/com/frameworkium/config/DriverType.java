package com.frameworkium.config;

import com.frameworkium.capture.ScreenshotCapture;
import com.frameworkium.listeners.CaptureListener;
import com.frameworkium.listeners.EventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.SessionNotFoundException;

import static com.frameworkium.config.SystemProperty.*;
import static com.frameworkium.config.DriverSetup.useRemoteDriver;

public abstract class DriverType {

    private WebDriverWrapper webDriverWrapper;

    protected final static Logger logger = LogManager.getLogger(DriverType.class);

    /**
     * Creates the Wrapped Driver object, and returns to the test
     *
     * @return - Wrapped WebDriver object
     */
    public void instantiate() {
        logger.info("Current Browser Selection: " + this);

        DesiredCapabilities caps = getDesiredCapabilities();
        logger.info("Caps: " + caps.toString());

        WebDriverWrapper eventFiringWD = new WebDriverWrapper(getWebDriverObject(caps));
        eventFiringWD.register(new EventListener());
        if (ScreenshotCapture.isRequired()) {
            eventFiringWD.register(new CaptureListener());
        }
        webDriverWrapper = eventFiringWD;
    }

    /**
     * Returns the WebDriverWrapper with the initialised driver inside
     *
     * @return - Initialised WebDriverWrapper
     */
    public WebDriverWrapper getDriver() {
        return webDriverWrapper;
    }

    /**
     * Determines whether tests have been ran to configure against a mobile
     *
     * @return - Boolean, whether using mobile or not
     */
    public static boolean isMobile() {
        return false;
    }

    /**
     * Maximises the browser window based on paramaters
     */
    public void maximiseBrowserWindow() {
        if (!MAXIMISE.isSpecified() || Boolean.parseBoolean(MAXIMISE.getValue())) {
            if((!useRemoteDriver() && !isNative()) || GRID_URL.isSpecified()) {
                webDriverWrapper.manage().window().maximize();
            }
        }
    }

    /**
     * Method to tear down the driver object, can be overiden
     */
    public void tearDownDriver() {
        webDriverWrapper.getWrappedDriver().quit();
    }

    /**
     * Reset the browser session based on whether it's been reset before
     */
    public boolean clearSession(boolean requiresReset) {
        if (requiresReset) {
            try {
                if (DriverType.isNative()) {
                    webDriverWrapper.getWrappedAppiumDriver().resetApp();
                } else {
                    webDriverWrapper.manage().deleteAllCookies();
                }
            } catch (SessionNotFoundException e) {
                logger.error("Session quit unexpectedly.", e);
            }
        }
        return true;
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
