package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.driver.AbstractDriver;
import com.frameworkium.core.ui.driver.remotes.BrowserStack;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static com.frameworkium.core.ui.driver.DriverSetup.Platform;

public class BrowserStackImpl extends AbstractDriver {

    private URL remoteURL;
    private Platform platform;
    private DesiredCapabilities desiredCapabilities;

    public BrowserStackImpl(
            Platform platform, DesiredCapabilities browserDesiredCapabilities) {

        this.platform = platform;
        desiredCapabilities = browserDesiredCapabilities;
        try {
            remoteURL = BrowserStack.getURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public DesiredCapabilities getDesiredCapabilities() {
        setCapabilitiesBasedOnPlatform();
        desiredCapabilities.setCapability("browserstack.debug", true);
        return desiredCapabilities;
    }

    public WebDriver getWebDriver(DesiredCapabilities capabilities) {
        return new RemoteWebDriver(remoteURL, capabilities);
    }

    private void setCapabilitiesBasedOnPlatform() {
        switch (platform) {
            case WINDOWS:
                desiredCapabilities.setCapability("os", "Windows");
                setDesktopCapability();
                break;
            case OSX:
                desiredCapabilities.setCapability("os", "OS X");
                setDesktopCapability();
                break;
            case ANDROID:
                desiredCapabilities.setCapability("platform", "ANDROID");
                desiredCapabilities.setCapability("browserName", "android");
                if (Property.DEVICE.isSpecified()) {
                    desiredCapabilities.setCapability(
                            "device", Property.DEVICE.getValue());
                }
                break;
            case IOS:
                desiredCapabilities.setCapability("platform", "MAC");
                if (Property.DEVICE.isSpecified()) {
                    desiredCapabilities.setCapability(
                            "device", Property.DEVICE.getValue());
                    desiredCapabilities.setCapability(
                            "browserName", Property.DEVICE.getValue().split(" ")[0]);
                }
                break;
        }
    }

    private void setDesktopCapability() {
        if (Property.PLATFORM_VERSION.isSpecified()) {
            desiredCapabilities.setCapability(
                    "os_version", Property.PLATFORM_VERSION.getValue());
        }
        if (Property.RESOLUTION.isSpecified()) {
            desiredCapabilities.setCapability(
                    "resolution", Property.RESOLUTION.getValue());
        }
        if (Property.BROWSER_VERSION.isSpecified()) {
            desiredCapabilities.setCapability(
                    "browser_version", Property.BROWSER_VERSION.getValue());
        }
    }
}
