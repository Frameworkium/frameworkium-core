package com.frameworkium.config.drivers;

import com.frameworkium.config.DriverType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class FirefoxImpl extends DriverType {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        return DesiredCapabilities.firefox();
    }

    @Override
    public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
        return new FirefoxDriver(capabilities);
    }

    /**
     * Firefox unfortunately doesn't clear sessions, only cookies.
     * Browser teardown and re-build is needed
     */
    @Override
    public boolean clearSession(boolean requiresReset) {
        if (requiresReset) {
            tearDownDriver();
            instantiate();
        }
        return true;
    }

}
