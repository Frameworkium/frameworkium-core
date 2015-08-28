package com.frameworkium.config.drivers;

import static com.frameworkium.config.SystemProperty.DEVICE;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class ChromeImpl extends ProxyableDriver {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability("chrome.switches", Arrays.asList("--no-default-browser-check"));
        HashMap<String, String> chromePreferences = new HashMap<String, String>();
        chromePreferences.put("profile.password_manager_enabled", "false");
        capabilities.setCapability("chrome.prefs", chromePreferences);

        // Use Chrome's built in device emulators
        // Specify browser=chrome, but also provide device name to use chrome's emulator
        if (DEVICE.isSpecified()) {
            Map<String, String> mobileEmulation = new HashMap<String, String>();
            mobileEmulation.put("deviceName", DEVICE.getValue());

            Map<String, Object> chromeOptions = new HashMap<String, Object>();
            chromeOptions.put("mobileEmulation", mobileEmulation);

            capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        }

        Proxy currentProxy = getProxy();
        if (currentProxy != null) {
            capabilities.setCapability(CapabilityType.PROXY, currentProxy);
        }

        return capabilities;
    }

    @Override
    public WebDriver getWebDriverObject(final DesiredCapabilities capabilities) {
        return new ChromeDriver(capabilities);
    }

}
