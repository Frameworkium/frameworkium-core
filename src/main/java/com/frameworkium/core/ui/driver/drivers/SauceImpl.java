package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.ui.driver.AbstractDriver;
import com.frameworkium.core.ui.driver.Driver;
import com.frameworkium.core.ui.driver.remotes.Sauce;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.URL;

import static com.frameworkium.core.common.properties.Property.*;
import static com.frameworkium.core.ui.driver.DriverSetup.Platform;

public class SauceImpl extends AbstractDriver {

    private Platform platform;
    private Capabilities capabilities;
    private URL remoteURL;

    public SauceImpl(Platform platform, Capabilities capabilities) {
        this.platform = platform;
        this.capabilities = capabilities;
        this.remoteURL = Sauce.getURL();
    }

    @Override
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

    @Override
    public WebDriver getWebDriver(Capabilities capabilities) {
        return new RemoteWebDriver(remoteURL, capabilities);
    }

    private MutableCapabilities getCapabilitiesBasedOnPlatform() {
        switch (platform) {
            case WINDOWS:
                return getDesktopCapabilities("Windows");
            case OSX:
                return getDesktopCapabilities("OS X");
            case ANDROID:
                return getAndroidCapabilities();
            case IOS:
                return getIOSCapabilities();
            default:
                throw new IllegalStateException(
                        "Unrecognised platform: " + platform);
        }
    }

    private MutableCapabilities getDesktopCapabilities(String platformName) {
        if (!PLATFORM_VERSION.isSpecified()) {
            throw new IllegalArgumentException(
                    "Platform version required for " + platformName + "  SauceLabs!");
        }
        MutableCapabilities caps = new MutableCapabilities(capabilities);
        caps.setCapability(
                "platform", platformName + " " + PLATFORM_VERSION.getValue());
        if (BROWSER_VERSION.isSpecified()) {
            caps.setCapability("version", BROWSER_VERSION.getValue());
        }
        return caps;
    }

    private MutableCapabilities getAndroidCapabilities() {
        MutableCapabilities caps = new MutableCapabilities(capabilities);
        caps.setCapability("platformName", "Android");
        if (PLATFORM_VERSION.isSpecified()) {
            caps.setCapability("version", PLATFORM_VERSION.getValue());
        }
        caps.setCapability("deviceName", "Android Emulator");
        setPortraitOrientation(caps);
        return caps;
    }

    private MutableCapabilities getIOSCapabilities() {
        MutableCapabilities caps = new MutableCapabilities(capabilities);
        caps.setCapability("deviceName", "iPhone .*");
        caps.setCapability("platformName", "iOS");
        if (PLATFORM_VERSION.isSpecified()) {
            caps.setCapability("version", PLATFORM_VERSION.getValue());
        }
        if (DEVICE.isSpecified()) {
            caps.setCapability("deviceName", DEVICE.getValue() + " Simulator");
        }
        setPortraitOrientation(caps);
        return caps;
    }

    private MutableCapabilities getAppiumCapabilities() {
        MutableCapabilities caps = new MutableCapabilities(capabilities);
        caps.setCapability(
                "app", "sauce-storage:" + new File(APP_PATH.getValue()).getName());
        caps.setCapability("appiumVersion", "1.4.10");
        setPortraitOrientation(caps);
        caps.setCapability("browserName", "");
        switch (platform) {
            case IOS:
                caps.setCapability("deviceName", "iPhone .*");
                return getAppiumCapabilities(caps, "iOS", "Simulator");
            case ANDROID:
                caps.setCapability("platformName", "Android");
                return getAppiumCapabilities(caps, "Android", "Emulator");
            default:
                throw new IllegalStateException("Appium is only available on iOS/Android");
        }
    }

    private MutableCapabilities getAppiumCapabilities(
            Capabilities commonCaps, String platformName, String emulatorOrSimulator) {
        MutableCapabilities caps = new MutableCapabilities(commonCaps);
        if (DEVICE.isSpecified()) {
            caps.setCapability(
                    "deviceName", DEVICE.getValue() + " " + emulatorOrSimulator);
        }
        caps.setCapability("platformName", platformName);
        if (PLATFORM_VERSION.isSpecified()) {
            caps.setCapability("platformVersion", PLATFORM_VERSION.getValue());
        }
        return caps;
    }

    private void setPortraitOrientation(MutableCapabilities caps) {
        caps.setCapability("deviceOrientation", "portrait");
    }
}
