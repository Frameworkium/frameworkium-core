package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.ui.driver.DriverType;
import com.frameworkium.core.ui.driver.remotes.BrowserStack;
import com.frameworkium.core.ui.properties.UIProperty;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static com.frameworkium.core.ui.driver.DriverSetup.SupportedPlatforms;

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
                if (UIProperty.DEVICE.isSpecified()) {
                    desiredCapabilities.setCapability("device", UIProperty.DEVICE.getValue());
                }
                break;
            case IOS:
                desiredCapabilities.setCapability("platform", "MAC");
                if (UIProperty.DEVICE.isSpecified()) {
                    desiredCapabilities.setCapability("device", UIProperty.DEVICE.getValue());
                    desiredCapabilities.setCapability("browserName", UIProperty.DEVICE.getValue().split(" ")[0]);
                }
                break;
        }
    }

    private void setDesktopCapability() {
        if (UIProperty.PLATFORM_VERSION.isSpecified()) {
            desiredCapabilities.setCapability("os_version", UIProperty.PLATFORM_VERSION.getValue());
        }
        if (UIProperty.RESOLUTION.isSpecified()) {
            desiredCapabilities.setCapability("resolution", UIProperty.RESOLUTION.getValue());
        }
        if (UIProperty.BROWSER_VERSION.isSpecified()) {
            desiredCapabilities.setCapability("browser_version", UIProperty.BROWSER_VERSION.getValue());
        }
    }
}
