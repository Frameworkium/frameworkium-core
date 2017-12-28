package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.driver.AbstractDriver;
import com.frameworkium.core.ui.driver.remotes.BrowserStack;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static com.frameworkium.core.ui.driver.DriverSetup.Platform;

public class BrowserStackImpl extends AbstractDriver {

    private URL remoteURL;
    private Platform platform;
    private Capabilities capabilities;

    /**
     * Implementation of driver for BrowserStack.
     */
    public BrowserStackImpl(Platform platform, Capabilities browserCapabilities) {

        this.platform = platform;
        capabilities = browserCapabilities;
        try {
            remoteURL = BrowserStack.getURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public Capabilities getCapabilities() {
        MutableCapabilities capabilities = getCapabilitiesBasedOnPlatform();
        capabilities.setCapability("browserstack.debug", true);
        return capabilities;
    }

    private MutableCapabilities getCapabilitiesBasedOnPlatform() {
        MutableCapabilities mutableCapabilities = new MutableCapabilities(capabilities);
        switch (platform) {
            case WINDOWS:
                mutableCapabilities.setCapability("os", "Windows");
                mutableCapabilities.merge(getDesktopCapability());
                break;
            case OSX:
                mutableCapabilities.setCapability("os", "OS X");
                mutableCapabilities.merge(getDesktopCapability());
                break;
            case ANDROID:
                mutableCapabilities.setCapability("platform", "ANDROID");
                mutableCapabilities.setCapability("browserName", "android");
                if (Property.DEVICE.isSpecified()) {
                    mutableCapabilities.setCapability(
                            "device", Property.DEVICE.getValue());
                }
                break;
            case IOS:
                mutableCapabilities.setCapability("platform", "MAC");
                if (Property.DEVICE.isSpecified()) {
                    mutableCapabilities.setCapability(
                            "device", Property.DEVICE.getValue());
                    mutableCapabilities.setCapability(
                            "browserName", Property.DEVICE.getValue().split(" ")[0]);
                }
                break;
            default:
                break;
        }
        return mutableCapabilities;
    }

    private MutableCapabilities getDesktopCapability() {
        MutableCapabilities mutableCapabilities = new MutableCapabilities(capabilities);
        if (Property.PLATFORM_VERSION.isSpecified()) {
            mutableCapabilities.setCapability(
                    "os_version", Property.PLATFORM_VERSION.getValue());
        }
        if (Property.RESOLUTION.isSpecified()) {
            mutableCapabilities.setCapability(
                    "resolution", Property.RESOLUTION.getValue());
        }
        if (Property.BROWSER_VERSION.isSpecified()) {
            mutableCapabilities.setCapability(
                    "browser_version", Property.BROWSER_VERSION.getValue());
        }
        return mutableCapabilities;
    }

    public WebDriver getWebDriver(Capabilities capabilities) {
        return new RemoteWebDriver(remoteURL, capabilities);
    }
}
