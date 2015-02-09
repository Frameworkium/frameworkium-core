package com.frameworkium.config;

import io.appium.java_client.AppiumDriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

public class WebDriverWrapper extends EventFiringWebDriver{

    private WebDriver driver;
    
    public WebDriverWrapper(WebDriver driver) {
        super(driver);
        this.driver = driver;
    }

    public WebDriver getWrappedDriver() {
        return driver;
    }
    
    public RemoteWebDriver getWrappedRemoteWebDriver() {
        WebDriver wd = getWrappedDriver();
        if(wd instanceof RemoteWebDriver) {
            return (RemoteWebDriver) wd;
        } else {
            throw new RuntimeException(wd + " is not an instance of RemoteWebDriver");
        }
    }
    
    public AppiumDriver getWrappedAppiumDriver() {
        WebDriver wd = getWrappedDriver();
        if(wd instanceof AppiumDriver) {
            return (AppiumDriver) wd;
        } else {
            throw new RuntimeException(wd + " is not an instance of AppiumDriver");
        }
    }
}
