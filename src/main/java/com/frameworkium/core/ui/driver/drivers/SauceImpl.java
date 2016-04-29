package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.driver.DriverType;
import com.frameworkium.core.ui.driver.remotes.Sauce;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static com.frameworkium.core.ui.driver.DriverSetup.SupportedPlatforms;

public class SauceImpl extends DriverType {

    private static URL remoteURL;
    private static SupportedPlatforms supportedPlatforms;
    private static DesiredCapabilities desiredCapabilities;

    public SauceImpl(SupportedPlatforms platform, DesiredCapabilities browserDesiredCapabilities) {
        supportedPlatforms = platform;
        desiredCapabilities = browserDesiredCapabilities;
        try {
            remoteURL = Sauce.getURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public DesiredCapabilities getDesiredCapabilities() {
        if (isNative()) {
            setAppiumCapabilities();
        } else {
            setCapabilitiesBasedOnPlatform();
        }
        desiredCapabilities.setCapability("capture-html", true);
        desiredCapabilities.setCapability("sauce-advisor", false);
        desiredCapabilities.setCapability("build", Property.BUILD.getValue());
        return desiredCapabilities;
    }

    public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
        return new RemoteWebDriver(remoteURL, capabilities);
    }

    private void setCapabilitiesBasedOnPlatform() {
        switch (supportedPlatforms) {
        case WINDOWS:
            if (Property.PLATFORM_VERSION.isSpecified()) {
                desiredCapabilities.setCapability(
                        "platform", "Windows " + Property.PLATFORM_VERSION.getValue());
            } else {
                logger.error("Platform version needs to be specified when using Windows & SauceLabs!");
            }
            if (Property.BROWSER_VERSION.isSpecified()) {
                desiredCapabilities.setCapability(
                        "version", Property.BROWSER_VERSION.getValue());
            }
            break;
        case OSX:
            if (Property.PLATFORM_VERSION.isSpecified()) {
                desiredCapabilities.setCapability(
                        "platform", "OS X " + Property.PLATFORM_VERSION.getValue());
            } else {
                logger.error("Platform version needs to be specified when using OSX & SauceLabs!");
            }
            if (Property.BROWSER_VERSION.isSpecified()) {
                desiredCapabilities.setCapability(
                        "version", Property.BROWSER_VERSION.getValue());
            }
            break;
        case ANDROID:
            desiredCapabilities = DesiredCapabilities.android();
            desiredCapabilities.setCapability("platform", "Linux");
            if (Property.PLATFORM_VERSION.isSpecified()) {
                desiredCapabilities.setCapability(
                        "version", Property.PLATFORM_VERSION.getValue());
            }
            desiredCapabilities.setCapability("deviceName", "Android Emulator");
            desiredCapabilities.setCapability("deviceOrientation", "portrait");
            break;
        case IOS:
            desiredCapabilities = DesiredCapabilities.iphone();
            desiredCapabilities.setCapability("platform", "OS X 10.10");
            if (Property.PLATFORM_VERSION.isSpecified()) {
                desiredCapabilities.setCapability(
                        "version", Property.PLATFORM_VERSION.getValue());
            }
            if (Property.DEVICE.isSpecified()) {
                desiredCapabilities.setCapability(
                        "deviceName", Property.DEVICE.getValue() + " Simulator");
            }
            desiredCapabilities.setCapability("deviceOrientation", "portrait");
            break;
        }
    }

    private void setAppiumCapabilities() {
        desiredCapabilities.setCapability(
                "app", "sauce-storage:" + new File(Property.APP_PATH.getValue()).getName());
        desiredCapabilities.setCapability("appiumVersion", "1.4.10");
        desiredCapabilities.setCapability("deviceOrientation", "portrait");
        switch (supportedPlatforms) {
        case IOS:
            desiredCapabilities = DesiredCapabilities.iphone();
            if (Property.DEVICE.isSpecified()) {
                desiredCapabilities.setCapability(
                        "deviceName", Property.DEVICE.getValue() + " Simulator");
            }
            desiredCapabilities.setCapability("platformName", "iOS");
            desiredCapabilities.setCapability("browserName", "");
            if (Property.PLATFORM_VERSION.isSpecified()) {
                desiredCapabilities.setCapability(
                        "platformVersion", Property.PLATFORM_VERSION.getValue());
            }
            break;
        case ANDROID:
            desiredCapabilities = DesiredCapabilities.android();
            if (Property.DEVICE.isSpecified()) {
                desiredCapabilities.setCapability(
                        "deviceName", Property.DEVICE.getValue() + " Emulator");
            }
            desiredCapabilities.setCapability("platformName", "Android");
            desiredCapabilities.setCapability("browserName", "");
            if (Property.PLATFORM_VERSION.isSpecified()) {
                desiredCapabilities.setCapability(
                        "platformVersion", Property.PLATFORM_VERSION.getValue());
            }
            break;
        default:
            logger.error("Appium is not available on any platform other than iOS/Android!");
        }
    }
}
