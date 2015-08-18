package com.frameworkium.config.browsers;

import com.frameworkium.config.DriverType;
import com.frameworkium.config.remotes.BrowserStack;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
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
        desiredCapabilities = setGeneralRemoteCapabilities(desiredCapabilities);
        desiredCapabilities = setCapabilitiesBasedOnPlatform(desiredCapabilities);

        //Enables Screenshots
        desiredCapabilities.setCapability("browserstack.debug", true);

        // The following three are browserstack specific
        // Set OS
        if (PLATFORM.isSpecified()) {
            desiredCapabilities.setCapability("os", PLATFORM.getValue());
        }
        // Set OS Version
        if (PLATFORM_VERSION.isSpecified()) {
            desiredCapabilities.setCapability("os_version", PLATFORM_VERSION.getValue());
        }
        // Set Device (rather than deviceName)
        if (DEVICE.isSpecified()) {
            desiredCapabilities.setCapability("device", DEVICE.getValue());
        }
        if (PLATFORM_VERSION.isSpecified()) {
            logger.error("Platform/OS version on mobile devices not supported by BrowserStack - supply a Device instead");
        }
        if(isMobile()) {
            //Set Appium Version
            desiredCapabilities.setCapability("appiumVersion", "1.3.4");
            if (DEVICE.isSpecified()) {
                desiredCapabilities.setCapability("device", DEVICE.getValue());
            } else {
                if (PLATFORM.getValue().equalsIgnoreCase("ios")) {
                    desiredCapabilities.setCapability("device", "iPhone 5");
                } else if (PLATFORM.getValue().equalsIgnoreCase("android")) {
                    desiredCapabilities.setCapability("device", "Samsung Galaxy S5");
                }
            }
        }
        return desiredCapabilities;
    }

    public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
        switch(supportedPlatform) {
            case ANDROID:
                return new AndroidDriver(remoteURL, capabilities);
            case IOS:
                return new IOSDriver(remoteURL, capabilities);
            default:
                return new RemoteWebDriver(remoteURL, capabilities);
        }
    }

    private DesiredCapabilities setCapabilitiesBasedOnPlatform(DesiredCapabilities desiredCapabilities) {
        switch(supportedPlatform) {
            case ANDROID:
                desiredCapabilities.setCapability("os", "android");
            case BROWSER:
                desiredCapabilities.setCapability("os", "android");
                desiredCapabilities.setCapability("browser", "Android");
                break;
            case IOS:
                desiredCapabilities.setCapability("os", "iOS");
                desiredCapabilities.setCapability("browser", DEVICE.getValue().split(" ")[0]);
                break;
        }
        return desiredCapabilities;
    }
}
