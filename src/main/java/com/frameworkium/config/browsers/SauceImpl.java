package com.frameworkium.config.browsers;

import com.frameworkium.config.DriverType;
import com.frameworkium.config.remotes.Sauce;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static com.frameworkium.config.SystemProperty.*;
import static com.frameworkium.config.SystemProperty.BROWSER;
import static com.frameworkium.config.SystemProperty.PLATFORM;
import static com.frameworkium.config.DriverSetup.SupportedPlatforms;

public class SauceImpl extends DriverType {

    private static URL remoteURL;
    private static SupportedPlatforms supportedPlatforms;

    public SauceImpl(SupportedPlatforms platform) {
        supportedPlatforms = platform;
        try {
            remoteURL = Sauce.getURL();
        }
        catch(MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities = setGeneralRemoteCapabilities(desiredCapabilities);
        desiredCapabilities = setCapabilitiesBasedOnPlatform(desiredCapabilities);

        // Enable HTML source capture
        desiredCapabilities.setCapability("capture-html", true);
        // Disable Sauce Advisor
        desiredCapabilities.setCapability("sauce-advisor", false);
        // Set build number
        desiredCapabilities.setCapability("build", BUILD.getValue());

        if (isNative()) {
            desiredCapabilities
                    .setCapability("app", "sauce-storage:" + new File(APP_PATH.getValue()).getName());
        }
        if(isMobile()) {
            //Set Appium Version
            desiredCapabilities.setCapability("appiumVersion", "1.3.4");
            if (PLATFORM_VERSION.isSpecified()) {
                desiredCapabilities.setCapability("platformVersion", PLATFORM_VERSION.getValue());
            } else {
                //Pick some defaults
                if (PLATFORM.getValue().equalsIgnoreCase("ios")) {
                    desiredCapabilities.setCapability("platformVersion", "8.1");

                } else if (PLATFORM.getValue().equalsIgnoreCase("android")) {
                    desiredCapabilities.setCapability("platformVersion", "5.0");
                }
            }
        }
        return desiredCapabilities;
    }

    public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
        return new RemoteWebDriver(remoteURL, capabilities);
    }

    private DesiredCapabilities setCapabilitiesBasedOnPlatform(DesiredCapabilities desiredCapabilities) {
        switch(supportedPlatforms) {
            case ANDROID:
                desiredCapabilities.setCapability("platformName", "Android");
            case BROWSER:
                desiredCapabilities.setCapability("browserName", "Browser");
                desiredCapabilities.setCapability("platformName", "Android");
                break;
            case IOS:
                desiredCapabilities.setCapability("platformName", "iOS");
                break;
        }
        return desiredCapabilities;
    }
}
