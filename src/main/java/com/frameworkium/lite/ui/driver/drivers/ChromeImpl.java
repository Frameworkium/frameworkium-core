package com.frameworkium.lite.ui.driver.drivers;

import com.frameworkium.lite.common.properties.Property;
import com.frameworkium.lite.ui.driver.AbstractDriver;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Collections;

public class ChromeImpl extends AbstractDriver {

    @Override
    public ChromeOptions getCapabilities() {
        ChromeOptions chromeOptions = new ChromeOptions();

        // useful defaults
        chromeOptions.setCapability(
                "chrome.switches",
                Collections.singletonList("--no-default-browser-check"));
        chromeOptions.setCapability(
                "chrome.prefs",
                ImmutableMap.of("profile.password_manager_enabled", "false"));

        // Workaround Docker/Travis issue
        if (Boolean.parseBoolean(System.getenv("CHROME_NO_SANDBOX"))) {
            chromeOptions.addArguments("--no-sandbox");
        }

        chromeOptions.setHeadless(Property.HEADLESS.getBoolean());
        return chromeOptions;
    }

    @Override
    public WebDriver getWebDriver(Capabilities capabilities) {
        final ChromeOptions chromeOptions;
        if (capabilities instanceof ChromeOptions) {
            chromeOptions = (ChromeOptions) capabilities;
        } else {
            chromeOptions = new ChromeOptions().merge(capabilities);
        }
        return new ChromeDriver(chromeOptions);
    }

}
