package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.driver.DriverType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.*;

public class ChromeImpl extends DriverType {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(
                "chrome.switches", Arrays.asList("--no-default-browser-check"));
        HashMap<String, String> chromePreferences = new HashMap<>();
        chromePreferences.put("profile.password_manager_enabled", "false");
        capabilities.setCapability("chrome.prefs", chromePreferences);

        // Use Chrome's built in device emulators
        // Specify browser=chrome, but also provide device name to use chrome's emulator
        if (Property.DEVICE.isSpecified()) {
            Map<String, String> mobileEmulation = new HashMap<>();
            mobileEmulation.put("deviceName", Property.DEVICE.getValue());

            Map<String, Object> chromeOptions = new HashMap<>();
            chromeOptions.put("mobileEmulation", mobileEmulation);

            capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        }
        return capabilities;
    }

    @Override
    public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
        return new ChromeDriver(capabilities);
    }

}
