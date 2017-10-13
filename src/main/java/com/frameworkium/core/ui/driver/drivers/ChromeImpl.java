package com.frameworkium.core.ui.driver.drivers;

import static java.util.Collections.singletonList;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.driver.AbstractDriver;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

public class ChromeImpl extends AbstractDriver {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(
                "chrome.switches",
                singletonList("--no-default-browser-check"));
        capabilities.setCapability(
                "chrome.prefs",
                ImmutableMap.of("profile.password_manager_enabled", "false"));

        // Use Chrome's built in device emulators
        // Specify browser=chrome, but also provide device name to use chrome's emulator
        if (Property.DEVICE.isSpecified()) {
            Map<String, Map<String, String>> chromeOptions = ImmutableMap.of(
                    "mobileEmulation", ImmutableMap.of(
                            "deviceName", Property.DEVICE.getValue()));

            capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        }

        // Allow user to provide their own user directory, for custom chrome profiles
        if (Property.CHROME_USER_DATA_DIR.isSpecified()) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments(
                    "user-data-dir=" + Property.CHROME_USER_DATA_DIR.getValue());

            capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        }

        return capabilities;
    }

    @Override
    public WebDriver getWebDriver(DesiredCapabilities capabilities) {
        return new ChromeDriver(capabilities);
    }

}
