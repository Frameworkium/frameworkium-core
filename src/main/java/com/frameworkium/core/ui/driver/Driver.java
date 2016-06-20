package com.frameworkium.core.ui.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import static com.frameworkium.core.common.properties.Property.APP_PATH;

public interface Driver {

    /** @return whether using mobile or not */
    static boolean isMobile() {
        return false;
    }

    /** @return whether a native app is being tested. */
    static boolean isNative() {
        return APP_PATH.isSpecified();
    }

    /** Reset the browser ready for another test. */
    void resetBrowser();

    /** Method to tear down the driver object. */
    void tearDown();

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

    /** @return WebDriverWrapper which wraps the initialised driver. */
    WebDriverWrapper getDriver();
}
