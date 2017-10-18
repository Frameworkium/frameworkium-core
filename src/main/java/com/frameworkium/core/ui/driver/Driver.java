package com.frameworkium.core.ui.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import static com.frameworkium.core.common.properties.Property.APP_PATH;

public interface Driver {

    /** Check whether the driver is for a mobile device. */
    static boolean isMobile() {
        return false;
    }

    /** Check whether the driver is for a native mobile app. */
    static boolean isNative() {
        return APP_PATH.isSpecified();
    }

    /** Method to tear down the driver object. */
    void tearDown();

    /** Method to set-up the driver object. */
    void initialise();

    /**
     * Implemented in each Driver Type to specify the capabilities of that browser.
     *
     * @return Desired Capabilities of each browser
     */
    DesiredCapabilities getDesiredCapabilities();

    /**
     * Returns the correct WebDriver object for the driver type.
     *
     * @param capabilities Capabilities of the browser
     * @return {@link WebDriver} object for the browser
     */
    WebDriver getWebDriver(DesiredCapabilities capabilities);

    /**
     * Getter for the driver that wraps the initialised driver.
     *
     * @return WebDriverWrapper
     */
    WebDriverWrapper getDriver();
}
