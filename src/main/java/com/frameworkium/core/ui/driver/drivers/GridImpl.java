package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.driver.AbstractDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static com.frameworkium.core.common.properties.Property.*;

public class GridImpl extends AbstractDriver {

    private URL remoteURL;
    private DesiredCapabilities desiredCapabilities;

    public GridImpl(DesiredCapabilities desiredCapabilities) {
        this.desiredCapabilities = desiredCapabilities;
        try {
            this.remoteURL = new URL(Property.GRID_URL.getValue());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public DesiredCapabilities getDesiredCapabilities() {
        if (BROWSER_VERSION.isSpecified()) {
            desiredCapabilities.setCapability("version", BROWSER_VERSION.getValue());
        }
        if (PLATFORM.isSpecified()) {
            desiredCapabilities.setCapability("platform", PLATFORM_VERSION.getValue());
        }
        if(APPLICATION_NAME.isSpecified()){
            desiredCapabilities.setCapability("applicationName", APPLICATION_NAME.getValue());
        }
        return desiredCapabilities;
    }

    public WebDriver getWebDriver(DesiredCapabilities capabilities) {
        return new RemoteWebDriver(remoteURL, capabilities);
    }
}
