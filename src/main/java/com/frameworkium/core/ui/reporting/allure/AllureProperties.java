package com.frameworkium.core.ui.reporting.allure;

import com.frameworkium.core.ui.properties.UIProperty;
import com.frameworkium.core.ui.tests.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class AllureProperties {
    private static final Logger logger = LogManager.getLogger(AllureProperties.class);


    public static Properties getUIProperties() {
        Properties props = new Properties();

        //Now get all UI-specific properties
        if (UIProperty.APP_PATH.isSpecified()) {
            props.setProperty("App Path", UIProperty.APP_PATH.getValue());
        }
        if (UIProperty.BROWSER.isSpecified()) {
            props.setProperty("Browser", UIProperty.BROWSER.getValue());
        }
        if (UIProperty.BROWSER_VERSION.isSpecified()) {
            props.setProperty("Browser Version", UIProperty.BROWSER_VERSION.getValue());
        }
        if (UIProperty.DEVICE.isSpecified()) {
            props.setProperty("Device Name", UIProperty.DEVICE.getValue());
        }
        if (UIProperty.GRID_URL.isSpecified()) {
            props.setProperty("Grid URL", UIProperty.GRID_URL.getValue());
        }
        if (UIProperty.PLATFORM.isSpecified()) {
            props.setProperty("Platform", UIProperty.PLATFORM.getValue());
        }
        if (UIProperty.PLATFORM_VERSION.isSpecified()) {
            props.setProperty("Platform Version", UIProperty.PLATFORM_VERSION.getValue());
        }
        if(BaseTest.userAgent != null) {
            props.setProperty("UserAgent", BaseTest.userAgent);
        }

        return props;
    }

}
