package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.driver.DriverType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class GridImpl extends DriverType {

    private static URL remoteURL;
    private static DesiredCapabilities desiredCapabilities;

    public GridImpl(DesiredCapabilities browserDesiredCapabilities) {
        desiredCapabilities = browserDesiredCapabilities;
        try {
            remoteURL = new URL(Property.GRID_URL.getValue());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public DesiredCapabilities getDesiredCapabilities() {
        if (Property.BROWSER_VERSION.isSpecified()) {
            desiredCapabilities.setCapability("version", Property.BROWSER_VERSION.getValue());
        }
        if (Property.PLATFORM.isSpecified()) {
            desiredCapabilities.setCapability("platform", Property.PLATFORM_VERSION.getValue());
        }
        return desiredCapabilities;
    }

    public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
        return new RemoteWebDriver(remoteURL, capabilities);
    }
}
