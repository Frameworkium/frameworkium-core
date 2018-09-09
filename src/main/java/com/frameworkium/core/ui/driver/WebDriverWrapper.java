package com.frameworkium.core.ui.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

public class WebDriverWrapper extends EventFiringWebDriver {

    private WebDriver driver;

    public WebDriverWrapper(WebDriver driver) {
        super(driver);
        this.driver = driver;
    }

    @Override
    public WebDriver getWrappedDriver() {
        return driver;
    }

    /**
     * Get remote web driver wrapper.
     */
    public RemoteWebDriver getWrappedRemoteWebDriver() {
        if (driver instanceof RemoteWebDriver) {
            return (RemoteWebDriver) driver;
        } else {
            throw new IllegalStateException(driver + " is not an instance of RemoteWebDriver");
        }
    }
}
