package com.bootstrapium.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

public class EFWebDriver extends EventFiringWebDriver{

    private WebDriver driver;
    
    public EFWebDriver(WebDriver driver) {
        super(driver);
        this.driver = driver;
    }

    public WebDriver getWrappedDriver() {
        return driver;
    }
}
