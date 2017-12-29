package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.ui.driver.AbstractDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static com.frameworkium.core.common.properties.Property.APP_PATH;

public class ElectronImpl extends AbstractDriver {

    private static URL remoteURL;

    static {
        try {
            remoteURL = new URL("http://localhost:9515");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Capabilities getCapabilities() {
        if (!APP_PATH.isSpecified()) {
            throw new IllegalStateException(
                    "App path must be specified when using Electron!");
        }

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setBinary(APP_PATH.getValue());
        return chromeOptions;
    }

    @Override
    public WebDriver getWebDriver(Capabilities capabilities) {
        return new RemoteWebDriver(remoteURL, capabilities);
    }
}
