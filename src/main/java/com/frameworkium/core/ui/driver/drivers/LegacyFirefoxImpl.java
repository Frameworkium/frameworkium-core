package com.frameworkium.core.ui.driver.drivers;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.firefox.FirefoxOptions;

public class LegacyFirefoxImpl extends FirefoxImpl {

    @Override
    public Capabilities getCapabilities() {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setLegacy(true);
        return firefoxOptions;
    }
}
