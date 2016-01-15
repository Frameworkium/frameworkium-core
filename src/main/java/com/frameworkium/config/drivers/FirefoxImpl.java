package com.frameworkium.config.drivers;

import com.frameworkium.config.DriverType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;

import static com.frameworkium.config.SystemProperty.FIREFOX_PROFILE;

public class FirefoxImpl extends DriverType {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        return DesiredCapabilities.firefox();
    }

    @Override
    public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
        if (FIREFOX_PROFILE.isSpecified()) {
            File profileFile = new File(FIREFOX_PROFILE.getValue());
            FirefoxProfile firefoxProfile;
            if (profileFile.isFile() && !profileFile.isDirectory()) {
                firefoxProfile = new FirefoxProfile(profileFile);
            }
            else {
                firefoxProfile = new ProfilesIni().getProfile(FIREFOX_PROFILE.getValue());
            }
            return new FirefoxDriver(firefoxProfile);
        }
        else {
            return new FirefoxDriver(capabilities);
        }
    }
}
