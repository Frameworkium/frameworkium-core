package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.ui.driver.AbstractDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * @deprecated will be removed once headless firefox and chrome are available
 */
@Deprecated
public class PhantomJSImpl extends AbstractDriver {

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
        capabilities.setCapability("takesScreenshot", true);
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS,
                new String[]{"--webdriver-loglevel=NONE"});
        return capabilities;
    }

    @Override
    public WebDriver getWebDriver(DesiredCapabilities capabilities) {
        return new PhantomJSDriver(capabilities);
    }

}
