package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.common.properties.CommonProperty;
import com.frameworkium.core.ui.driver.DriverType;
import com.frameworkium.core.ui.driver.remotes.Sauce;
import com.frameworkium.core.ui.properties.UIProperty;
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
        }
        catch(MalformedURLException e) {
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
        desiredCapabilities.setCapability("build", CommonProperty.BUILD.getValue());
        return desiredCapabilities;
    }

    public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
        return new RemoteWebDriver(remoteURL, capabilities);
    }

    private void setCapabilitiesBasedOnPlatform() {
        switch(supportedPlatforms) {
            case WINDOWS:
                if (UIProperty.PLATFORM_VERSION.isSpecified()) {
                    desiredCapabilities.setCapability("platform", "Windows " + UIProperty.PLATFORM_VERSION.getValue());
                }
                else {
                    logger.error("Platform version needs to be specified when using Windows & SauceLabs!");
                }
                if (UIProperty.BROWSER_VERSION.isSpecified()) {
                    desiredCapabilities.setCapability("version", UIProperty.BROWSER_VERSION.getValue());
                }
                break;
            case OSX:
                if (UIProperty.PLATFORM_VERSION.isSpecified()) {
                    desiredCapabilities.setCapability("platform", "OS X " + UIProperty.PLATFORM_VERSION.getValue());
                }
                else {
                    logger.error("Platform version needs to be specified when using OSX & SauceLabs!");
                }
                if (UIProperty.BROWSER_VERSION.isSpecified()) {
                    desiredCapabilities.setCapability("version", UIProperty.BROWSER_VERSION.getValue());
                }
                break;
            case ANDROID:
                desiredCapabilities = DesiredCapabilities.android();
                desiredCapabilities.setCapability("platform", "Linux");
                if (UIProperty.PLATFORM_VERSION.isSpecified()) {
                    desiredCapabilities.setCapability("version", UIProperty.PLATFORM_VERSION.getValue());
                }
                desiredCapabilities.setCapability("deviceName", "Android Emulator");
                desiredCapabilities.setCapability("deviceOrientation", "portrait");
                break;
            case IOS:
                desiredCapabilities = DesiredCapabilities.iphone();
                desiredCapabilities.setCapability("platform", "OS X 10.10");
                if (UIProperty.PLATFORM_VERSION.isSpecified()) {
                    desiredCapabilities.setCapability("version", UIProperty.PLATFORM_VERSION.getValue());
                }
                if (UIProperty.DEVICE.isSpecified()) {
                    desiredCapabilities.setCapability("deviceName", UIProperty.DEVICE.getValue()+" Simulator");
                }
                desiredCapabilities.setCapability("deviceOrientation", "portrait");
                break;
        }
    }

    private void setAppiumCapabilities() {
        desiredCapabilities
                .setCapability("app", "sauce-storage:" + new File(UIProperty.APP_PATH.getValue()).getName());
        desiredCapabilities.setCapability("appiumVersion", "1.4.10");
        desiredCapabilities.setCapability("deviceOrientation", "portrait");
        switch (supportedPlatforms) {
            case IOS:
                desiredCapabilities = DesiredCapabilities.iphone();
                if (UIProperty.DEVICE.isSpecified()) {
                    desiredCapabilities.setCapability("deviceName", UIProperty.DEVICE.getValue()+" Simulator");
                }
                desiredCapabilities.setCapability("platformName", "iOS");
                desiredCapabilities.setCapability("browserName", "");
                if (UIProperty.PLATFORM_VERSION.isSpecified()) {
                    desiredCapabilities.setCapability("platformVersion", UIProperty.PLATFORM_VERSION.getValue());
                }
                break;
            case ANDROID:
                desiredCapabilities = DesiredCapabilities.android();
                if (UIProperty.DEVICE.isSpecified()) {
                    desiredCapabilities.setCapability("deviceName", UIProperty.DEVICE.getValue()+" Emulator");
                }
                desiredCapabilities.setCapability("platformName", "Android");
                desiredCapabilities.setCapability("browserName", "");
                if (UIProperty.PLATFORM_VERSION.isSpecified()) {
                    desiredCapabilities.setCapability("platformVersion", UIProperty.PLATFORM_VERSION.getValue());
                }
                break;
            default:
                logger.error("Appium is not available on any platform other than iOS/Android!");
        }
    }
}
