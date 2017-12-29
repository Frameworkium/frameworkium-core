package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.driver.AbstractDriver;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static java.util.Collections.singletonList;

public class ChromeImpl extends AbstractDriver {

    @Override
    public Capabilities getCapabilities() {
        ChromeOptions chromeOptions = new ChromeOptions();
        // useful defaults
        chromeOptions.setCapability(
                "chrome.switches",
                singletonList("--no-default-browser-check"));
        chromeOptions.setCapability(
                "chrome.prefs",
                ImmutableMap.of("profile.password_manager_enabled", "false"));

        // Use Chrome's built in device emulators
        if (Property.DEVICE.isSpecified()) {
            chromeOptions.setExperimentalOption(
                    "mobileEmulation",
                    ImmutableMap.of("deviceName", Property.DEVICE.getValue()));
        }

        // Allow user to provide their own user directory, for custom chrome profiles
        if (Property.CHROME_USER_DATA_DIR.isSpecified()) {
            chromeOptions.addArguments(
                    "user-data-dir=" + Property.CHROME_USER_DATA_DIR.getValue());
        }

        return chromeOptions;
    }

    @Override
    public WebDriver getWebDriver(Capabilities capabilities) {
        return new ChromeDriver(new ChromeOptions().merge(capabilities));
    }

}
