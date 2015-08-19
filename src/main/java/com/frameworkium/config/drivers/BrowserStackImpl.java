package com.frameworkium.config.drivers;

import com.frameworkium.config.DriverType;
import com.frameworkium.config.remotes.BrowserStack;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static com.frameworkium.config.SystemProperty.*;
import static com.frameworkium.config.DriverSetup.SupportedPlatforms;

public class BrowserStackImpl extends DriverType {

    private static URL remoteURL;
    private static SupportedPlatforms supportedPlatform;
    private static DesiredCapabilities desiredCapabilities;

    public BrowserStackImpl(SupportedPlatforms platform, DesiredCapabilities browserDesiredCapabilities) {
        supportedPlatform = platform;
        desiredCapabilities = browserDesiredCapabilities;
        try {
            remoteURL = BrowserStack.getURL();
        }
        catch(MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public DesiredCapabilities getDesiredCapabilities() {
        setCapabilitiesBasedOnPlatform();
        desiredCapabilities.setCapability("browserstack.debug", true);
        return desiredCapabilities;
    }

    public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
        return new RemoteWebDriver(remoteURL, capabilities);
    }

    private void setCapabilitiesBasedOnPlatform() {
        switch(supportedPlatform) {
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
                if (DEVICE.isSpecified()) {
                    desiredCapabilities.setCapability("device", DEVICE.getValue());
                }
                break;
            case IOS:
                desiredCapabilities.setCapability("platform", "MAC");
                if (DEVICE.isSpecified()) {
                    desiredCapabilities.setCapability("device", DEVICE.getValue());
                    desiredCapabilities.setCapability("browserName", DEVICE.getValue().split(" ")[0]);
                }
                break;
        }
    }

    private void setDesktopCapability() {
        if (PLATFORM_VERSION.isSpecified()) {
            desiredCapabilities.setCapability("os_version", PLATFORM_VERSION.getValue());
        }
        if (RESOLUTION.isSpecified()) {
            desiredCapabilities.setCapability("resolution", RESOLUTION.getValue());
        }
        if (BROWSER_VERSION.isSpecified()) {
            desiredCapabilities.setCapability("browser_version", BROWSER_VERSION.getValue());
        }
    }
}
