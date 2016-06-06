package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.ui.driver.AbstractDriver;
import com.frameworkium.core.ui.driver.Driver;
import com.frameworkium.core.ui.driver.remotes.Sauce;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static com.frameworkium.core.common.properties.Property.*;
import static com.frameworkium.core.ui.driver.DriverSetup.Platform;

public class SauceImpl extends AbstractDriver {

    private URL remoteURL;
    private Platform platform;
    private DesiredCapabilities desiredCapabilities;

    public SauceImpl(Platform platform, DesiredCapabilities desiredCapabilities) {
        this.platform = platform;
        this.desiredCapabilities = desiredCapabilities;
        try {
            this.remoteURL = Sauce.getURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public DesiredCapabilities getDesiredCapabilities() {
        if (Driver.isNative()) {
            setAppiumCapabilities();
        } else {
            setCapabilitiesBasedOnPlatform();
        }
        desiredCapabilities.setCapability("capture-html", true);
        desiredCapabilities.setCapability("sauce-advisor", false);
        desiredCapabilities.setCapability("build", BUILD.getValue());
        return desiredCapabilities;
    }

    public WebDriver getWebDriver(DesiredCapabilities capabilities) {
        return new RemoteWebDriver(remoteURL, capabilities);
    }

    private void setCapabilitiesBasedOnPlatform() {
        switch (platform) {
            case WINDOWS:
                if (PLATFORM_VERSION.isSpecified()) {
                    desiredCapabilities.setCapability(
                            "platform", "Windows " + PLATFORM_VERSION.getValue());
                } else {
                    logger.error("Platform version needs to be specified when using Windows & SauceLabs!");
                }
                if (BROWSER_VERSION.isSpecified()) {
                    desiredCapabilities.setCapability(
                            "version", BROWSER_VERSION.getValue());
                }
                break;
            case OSX:
                if (PLATFORM_VERSION.isSpecified()) {
                    desiredCapabilities.setCapability(
                            "platform", "OS X " + PLATFORM_VERSION.getValue());
                } else {
                    logger.error("Platform version needs to be specified when using OSX & SauceLabs!");
                }
                if (BROWSER_VERSION.isSpecified()) {
                    desiredCapabilities.setCapability(
                            "version", BROWSER_VERSION.getValue());
                }
                break;
            case ANDROID:
                desiredCapabilities = DesiredCapabilities.android();
                desiredCapabilities.setCapability("platform", "Linux");
                if (PLATFORM_VERSION.isSpecified()) {
                    desiredCapabilities.setCapability(
                            "version", PLATFORM_VERSION.getValue());
                }
                desiredCapabilities.setCapability("deviceName", "Android Emulator");
                desiredCapabilities.setCapability("deviceOrientation", "portrait");
                break;
            case IOS:
                desiredCapabilities = DesiredCapabilities.iphone();
                desiredCapabilities.setCapability("platform", "OS X 10.10");
                if (PLATFORM_VERSION.isSpecified()) {
                    desiredCapabilities.setCapability(
                            "version", PLATFORM_VERSION.getValue());
                }
                if (DEVICE.isSpecified()) {
                    desiredCapabilities.setCapability(
                            "deviceName", DEVICE.getValue() + " Simulator");
                }
                desiredCapabilities.setCapability("deviceOrientation", "portrait");
                break;
        }
    }

    private void setAppiumCapabilities() {
        desiredCapabilities.setCapability(
                "app", "sauce-storage:" + new File(APP_PATH.getValue()).getName());
        desiredCapabilities.setCapability("appiumVersion", "1.4.10");
        desiredCapabilities.setCapability("deviceOrientation", "portrait");
        switch (platform) {
            case IOS:
                desiredCapabilities = DesiredCapabilities.iphone();
                if (DEVICE.isSpecified()) {
                    desiredCapabilities.setCapability(
                            "deviceName", DEVICE.getValue() + " Simulator");
                }
                desiredCapabilities.setCapability("platformName", "iOS");
                desiredCapabilities.setCapability("browserName", "");
                if (PLATFORM_VERSION.isSpecified()) {
                    desiredCapabilities.setCapability(
                            "platformVersion", PLATFORM_VERSION.getValue());
                }
                break;
            case ANDROID:
                desiredCapabilities = DesiredCapabilities.android();
                if (DEVICE.isSpecified()) {
                    desiredCapabilities.setCapability(
                            "deviceName", DEVICE.getValue() + " Emulator");
                }
                desiredCapabilities.setCapability("platformName", "Android");
                desiredCapabilities.setCapability("browserName", "");
                if (PLATFORM_VERSION.isSpecified()) {
                    desiredCapabilities.setCapability(
                            "platformVersion", PLATFORM_VERSION.getValue());
                }
                break;
            default:
                logger.error("Appium is only available on iOS/Android!");
        }
    }
}
