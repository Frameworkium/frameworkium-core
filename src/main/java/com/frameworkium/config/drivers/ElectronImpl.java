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
        if (!APP_PATH.isSpecified()) {
            logger.error("App path must be specified when using Electron!");
        } else {
            chromeOptions.put("binary", APP_PATH.getValue());
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
