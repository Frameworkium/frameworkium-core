package com.frameworkium.config.drivers;

import com.frameworkium.config.DriverType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.HashMap;
import java.util.Map;

import static com.frameworkium.config.SystemProperty.APP_PATH;

public class ElectronImpl extends DriverType {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {

        ChromeOptions options = new ChromeOptions();
        if (!APP_PATH.isSpecified()) {
            logger.error("App path must be specified when using Electron!");
        } else {
            options.setBinary(APP_PATH.getValue());
        }
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities().chrome();
        desiredCapabilities.setCapability("host", "localhost");
        desiredCapabilities.setCapability("port", 9515);
        desiredCapabilities.setCapability("chromeOptions", options);
        return desiredCapabilities;
    }

    @Override
    public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
        return new ChromeDriver(capabilities);
    }

}
