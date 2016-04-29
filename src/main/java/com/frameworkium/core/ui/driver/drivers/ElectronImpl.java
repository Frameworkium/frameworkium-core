package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.ui.driver.DriverType;
import com.frameworkium.core.common.properties.Property;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ElectronImpl extends DriverType {

    private static URL remoteURL;

    static {
        try {
            remoteURL = new URL("http://localhost:9515");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        Map<String, String> chromeOptions = new HashMap<>();
        if (!Property.APP_PATH.isSpecified()) {
            logger.error("App path must be specified when using Electron!");
        } else {
            chromeOptions.put("binary", Property.APP_PATH.getValue());
        }
        DesiredCapabilities desiredCapabilities = DesiredCapabilities.chrome();
        desiredCapabilities.setCapability("browserName", "chrome");
        desiredCapabilities.setCapability("chromeOptions", chromeOptions);
        return desiredCapabilities;
    }

    @Override
    public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
        return new RemoteWebDriver(remoteURL, capabilities);
    }
}
