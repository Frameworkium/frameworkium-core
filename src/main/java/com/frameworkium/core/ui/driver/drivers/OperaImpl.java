package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.ui.driver.AbstractDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Driver for the Opera browser.
 *
 * @deprecated will be removed from core soon.
 *         If Opera is required then a project specific DriverImpl can
 *         be created with the new CustomDriverImpl feature.
 */
@Deprecated
public class OperaImpl extends AbstractDriver {

    @Override
    public Capabilities getCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.opera();
        capabilities.setCapability("opera.arguments", "-nowin -nomail");
        return capabilities;
    }

    @Override
    public WebDriver getWebDriver(Capabilities capabilities) {
        return new OperaDriver(capabilities);
    }

}
