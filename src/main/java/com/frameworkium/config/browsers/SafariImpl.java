package com.frameworkium.config.browsers;

import com.frameworkium.config.DriverType;
import com.frameworkium.config.remotes.BrowserStack;
import com.frameworkium.config.remotes.Sauce;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.frameworkium.config.SystemProperty.DEVICE;

public class SafariImpl extends DriverType {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        if (isMobile()) {
            return new DesiredCapabilities();
        } else {
            DesiredCapabilities capabilities = DesiredCapabilities.safari();
            capabilities.setCapability("safari.cleanSession", true);
            return capabilities;
        }
    }

    @Override
    public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
        if (isMobile()) {
            throw new IllegalArgumentException(
                    "seleniumGridURL or sauceUser and sauceKey must be specified when running on iOS");
        } else {
            return new SafariDriver(capabilities);
        }
    }

}
