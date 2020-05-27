package com.frameworkium.lite.ui.driver.drivers;

import com.frameworkium.lite.common.properties.Property;
import com.frameworkium.lite.ui.driver.AbstractDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static com.frameworkium.lite.common.properties.Property.BROWSER_VERSION;

public class GridImpl extends AbstractDriver {

    private final URL remoteURL;
    private final Capabilities capabilities;

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
        return mutableCapabilities;
    }

    @Override
    public WebDriver getWebDriver(Capabilities capabilities) {
        return new RemoteWebDriver(remoteURL, capabilities);
    }
}
