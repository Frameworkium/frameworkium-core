package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.driver.AbstractDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;

public class FirefoxImpl extends AbstractDriver {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        return DesiredCapabilities.firefox();
    }

    @Override
    public WebDriver getWebDriver(DesiredCapabilities capabilities) {
        if (Property.FIREFOX_PROFILE.isSpecified()) {
            FirefoxProfile firefoxProfile;
            String fireFoxProfile = Property.FIREFOX_PROFILE.getValue();
            File profileFile = new File(fireFoxProfile);
            if (profileFile.isFile() || profileFile.isDirectory()) {
                firefoxProfile = new FirefoxProfile(profileFile);
            } else {
                firefoxProfile = new ProfilesIni().getProfile(fireFoxProfile);
            }
            return new FirefoxDriver(firefoxProfile);
        } else {
            return new FirefoxDriver(capabilities);
        }
    }
}
