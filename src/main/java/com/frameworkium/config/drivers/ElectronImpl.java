package com.frameworkium.config.drivers;

import com.frameworkium.config.DriverType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.frameworkium.config.SystemProperty.APP_PATH;
import static com.frameworkium.config.SystemProperty.GRID_URL;

public class ElectronImpl extends DriverType {

    private static URL remoteURL;

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        Map<String, String> chromeOptions = new HashMap<>();
        if (!APP_PATH.isSpecified()) {
            logger.error("App path must be specified when using Electron!");
        } else {
            chromeOptions.put("binary", APP_PATH.getValue());
        }
        try {
            if (GRID_URL.isSpecified()) {
                remoteURL = new URL(GRID_URL.getValue());
            }
            else {
                remoteURL = new URL("http://localhost:9515");
            }
        }
        catch(MalformedURLException e) {
            throw new RuntimeException(e);
        }
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("browserName", "chrome");
        desiredCapabilities.setCapability("chromeOptions", chromeOptions);
        return desiredCapabilities;
    }

    @Override
    public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
        return new RemoteWebDriver(remoteURL ,capabilities);
    }
}
