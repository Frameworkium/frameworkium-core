package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.driver.DriverType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;

public class FirefoxImpl extends DriverType {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        return DesiredCapabilities.firefox();
    }

    @Override
    public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
        if (Property.FIREFOX_PROFILE.isSpecified()) {
            File profileFile = new File(Property.FIREFOX_PROFILE.getValue());
            FirefoxProfile firefoxProfile;
            if (profileFile.isFile() || profileFile.isDirectory()) {
                firefoxProfile = new FirefoxProfile(profileFile);
            } else {
                firefoxProfile = new ProfilesIni().getProfile(
                        Property.FIREFOX_PROFILE.getValue());
            }
            return new FirefoxDriver(firefoxProfile);
        } else {
            return new FirefoxDriver(capabilities);
        }
    }
}
