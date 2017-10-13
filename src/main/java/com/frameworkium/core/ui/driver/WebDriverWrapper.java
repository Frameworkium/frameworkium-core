package com.frameworkium.core.ui.driver;

import io.appium.java_client.AppiumDriver;
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
     * @return
     */
    public RemoteWebDriver getWrappedRemoteWebDriver() {
        WebDriver wd = getWrappedDriver();
        if (wd instanceof RemoteWebDriver) {
            return (RemoteWebDriver) wd;
        } else {
            throw new IllegalStateException(wd + " is not an instance of RemoteWebDriver");
        }
    }

    /**
     * Get Appium Driver.
     * @return
     */
    public AppiumDriver getWrappedAppiumDriver() {
        WebDriver wd = getWrappedDriver();
        if (wd instanceof AppiumDriver) {
            return (AppiumDriver) wd;
        } else {
            throw new IllegalStateException(wd + " is not an instance of AppiumDriver");
        }
    }
}
