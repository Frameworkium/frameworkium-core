package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.ui.driver.AbstractDriver;
import com.frameworkium.core.ui.driver.Driver;
import com.frameworkium.core.ui.driver.remotes.Sauce;
import org.openqa.selenium.*;
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
    private Capabilities capabilities;

    /**
     * Implementation of driver for SauceLbs.
     */
    public SauceImpl(Platform platform, Capabilities capabilities) {
        this.platform = platform;
        this.capabilities = capabilities;
        try {
            this.remoteURL = Sauce.getURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /** {@inheritDoc} */
    public Capabilities getCapabilities() {
        MutableCapabilities mutableCapabilities;
        if (Driver.isNative()) {
            mutableCapabilities = getAppiumCapabilities();
        } else {
            mutableCapabilities = getCapabilitiesBasedOnPlatform();
        }
        mutableCapabilities.setCapability("capture-html", true);
        mutableCapabilities.setCapability("sauce-advisor", false);
        mutableCapabilities.setCapability("build", BUILD.getValue());
        return mutableCapabilities;
    }

    public WebDriver getWebDriver(Capabilities capabilities) {
        return new RemoteWebDriver(remoteURL, capabilities);
    }

    private MutableCapabilities getCapabilitiesBasedOnPlatform() {
        MutableCapabilities mutableCapabilities = new MutableCapabilities(capabilities);
        switch (platform) {
            case WINDOWS:
                if (PLATFORM_VERSION.isSpecified()) {
                    mutableCapabilities.setCapability(
                            "platform", "Windows " + PLATFORM_VERSION.getValue());
                } else {
                    logger.error("Platform version needs to be specified when using Windows & SauceLabs!");
                }
                if (BROWSER_VERSION.isSpecified()) {
                    mutableCapabilities.setCapability(
                            "version", BROWSER_VERSION.getValue());
                }
                break;
            case OSX:
                if (PLATFORM_VERSION.isSpecified()) {
                    mutableCapabilities.setCapability(
                            "platform", "OS X " + PLATFORM_VERSION.getValue());
                } else {
                    logger.error("Platform version needs to be specified when using OSX & SauceLabs!");
                }
                if (BROWSER_VERSION.isSpecified()) {
                    mutableCapabilities.setCapability(
                            "version", BROWSER_VERSION.getValue());
                }
                break;
            case ANDROID:
                mutableCapabilities = DesiredCapabilities.android();
                mutableCapabilities.setCapability("platform", "Linux");
                if (PLATFORM_VERSION.isSpecified()) {
                    mutableCapabilities.setCapability(
                            "version", PLATFORM_VERSION.getValue());
                }
                mutableCapabilities.setCapability("deviceName", "Android Emulator");
                mutableCapabilities.setCapability("deviceOrientation", "portrait");
                break;
            case IOS:
                mutableCapabilities = DesiredCapabilities.iphone();
                mutableCapabilities.setCapability("platform", "OS X 10.10");
                if (PLATFORM_VERSION.isSpecified()) {
                    mutableCapabilities.setCapability(
                            "version", PLATFORM_VERSION.getValue());
                }
                if (DEVICE.isSpecified()) {
                    mutableCapabilities.setCapability(
                            "deviceName", DEVICE.getValue() + " Simulator");
                }
                mutableCapabilities.setCapability("deviceOrientation", "portrait");
                break;
            default:
                throw new IllegalStateException("Unrecognised platform" + platform);
        }
        return mutableCapabilities;
    }

    private MutableCapabilities getAppiumCapabilities() {
        MutableCapabilities mutableCapabilities = new MutableCapabilities(capabilities);
        mutableCapabilities.setCapability(
                "app", "sauce-storage:" + new File(APP_PATH.getValue()).getName());
        mutableCapabilities.setCapability("appiumVersion", "1.4.10");
        mutableCapabilities.setCapability("deviceOrientation", "portrait");
        switch (platform) {
            case IOS:
                mutableCapabilities = DesiredCapabilities.iphone();
                if (DEVICE.isSpecified()) {
                    mutableCapabilities.setCapability(
                            "deviceName", DEVICE.getValue() + " Simulator");
                }
                mutableCapabilities.setCapability("platformName", "iOS");
                mutableCapabilities.setCapability("browserName", "");
                if (PLATFORM_VERSION.isSpecified()) {
                    mutableCapabilities.setCapability(
                            "platformVersion", PLATFORM_VERSION.getValue());
                }
                break;
            case ANDROID:
                mutableCapabilities = DesiredCapabilities.android();
                if (DEVICE.isSpecified()) {
                    mutableCapabilities.setCapability(
                            "deviceName", DEVICE.getValue() + " Emulator");
                }
                mutableCapabilities.setCapability("platformName", "Android");
                mutableCapabilities.setCapability("browserName", "");
                if (PLATFORM_VERSION.isSpecified()) {
                    mutableCapabilities.setCapability(
                            "platformVersion", PLATFORM_VERSION.getValue());
                }
                break;
            default:
                throw new IllegalStateException("Appium is only available on iOS/Android");
        }
        return mutableCapabilities;
    }
}
