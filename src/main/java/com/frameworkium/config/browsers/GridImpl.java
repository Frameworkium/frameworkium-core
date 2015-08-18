package com.frameworkium.config.browsers;

import com.frameworkium.config.DriverType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static com.frameworkium.config.SystemProperty.*;
import static com.frameworkium.config.DriverSetup.SupportedPlatforms;

public class GridImpl extends DriverType {

    private static URL remoteURL;
    private static DesiredCapabilities desiredCapabilities;

    public GridImpl(DesiredCapabilities browserDesiredCapabilities) {
        desiredCapabilities = browserDesiredCapabilities;
        try {
            remoteURL = new URL(GRID_URL.getValue());
        }
        catch(MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public DesiredCapabilities getDesiredCapabilities() {
        desiredCapabilities = setGeneralRemoteCapabilities(desiredCapabilities);
        return desiredCapabilities;
    }

    public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
        return new RemoteWebDriver(remoteURL, capabilities);
    }
}
