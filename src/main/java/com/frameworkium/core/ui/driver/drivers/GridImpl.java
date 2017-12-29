package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.driver.AbstractDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static com.frameworkium.core.common.properties.Property.*;

public class GridImpl extends AbstractDriver {

    private URL remoteURL;
    private Capabilities capabilities;

    /**
     * Implementation of driver for the Selenium Grid .
     */
    public GridImpl(Capabilities capabilities) {
        this.capabilities = capabilities;
        try {
            this.remoteURL = new URL(Property.GRID_URL.getValue());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Capabilities getCapabilities() {
        MutableCapabilities mutableCapabilities = new MutableCapabilities(capabilities);
        if (BROWSER_VERSION.isSpecified()) {
            mutableCapabilities.setCapability("version", BROWSER_VERSION.getValue());
        }
        if (PLATFORM.isSpecified()) {
            mutableCapabilities.setCapability("platform", PLATFORM_VERSION.getValue());
        }
        if (APPLICATION_NAME.isSpecified()) {
            mutableCapabilities.setCapability("applicationName", APPLICATION_NAME.getValue());
        }
        return mutableCapabilities;
    }

    @Override
    public WebDriver getWebDriver(Capabilities capabilities) {
        return new RemoteWebDriver(remoteURL, capabilities);
    }
}
