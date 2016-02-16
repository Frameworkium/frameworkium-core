package com.frameworkium.ui.config.drivers;

import com.frameworkium.ui.config.DriverType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

public class PhantomJSImpl extends DriverType {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
        capabilities.setCapability("takesScreenshot", true);
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS,
                new String[]{"--webdriver-loglevel=NONE"});
        return capabilities;
    }

    @Override
    public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
        return new PhantomJSDriver(capabilities);
    }

}
