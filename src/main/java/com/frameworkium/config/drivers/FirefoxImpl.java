package com.frameworkium.config.drivers;

import com.frameworkium.config.DriverType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.DesiredCapabilities;

import static com.frameworkium.config.SystemProperty.FIREFOX_PROFILE;

public class FirefoxImpl extends DriverType {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        return DesiredCapabilities.firefox();
    }

    @Override
    public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
        if (FIREFOX_PROFILE.isSpecified()) {
            return new FirefoxDriver(new ProfilesIni().getProfile(FIREFOX_PROFILE.getValue()));
        }
        else {
            return new FirefoxDriver(capabilities);
        }
    }
}
