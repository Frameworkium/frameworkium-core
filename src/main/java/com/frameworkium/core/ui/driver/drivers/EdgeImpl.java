package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.ui.driver.AbstractDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class EdgeImpl extends AbstractDriver {

    @Override
    public EdgeOptions getCapabilities() {
        return new EdgeOptions();
    }

    @Override
    public WebDriver getWebDriver(Capabilities capabilities) {
        final EdgeOptions edgeOptions;
        if (capabilities instanceof EdgeOptions) {
            edgeOptions = (EdgeOptions) capabilities;
        } else {
            edgeOptions = new EdgeOptions().merge(capabilities);
        }
        return new EdgeDriver(edgeOptions);
        
    }
}
