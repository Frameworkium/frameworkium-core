package com.frameworkium.core.common.reporting.allure;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.tests.BaseTest;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AllureProperties {
    private static final Logger logger = LogManager.getLogger(AllureProperties.class);

    public static void create() {
        FileOutputStream fos = null;
        try {
            Properties props = new Properties();
            fos = new FileOutputStream("target/allure-results/environment-common.properties");

            props.putAll(getCommonProps());

            props.store(fos, "See https://github.com/allure-framework/allure-core/wiki/Environment");

            fos.close();
        } catch (IOException e) {
            logger.error("IO problem when writing allure properties file", e);
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    private static Properties getCommonProps() {
        Properties props = new Properties();

        if (Property.BUILD.isSpecified()) {
            props.setProperty("Build", Property.BUILD.getValue());
        }
        if (Property.RESULT_VERSION.isSpecified()) {
            props.setProperty("Jira Result Version", Property.RESULT_VERSION.getValue());
        }
        if (Property.JIRA_URL.isSpecified()) {
            props.setProperty("allure.issues.tracker.pattern", Property.JIRA_URL.getValue() + "/browse/%s");
            props.setProperty("allure.tests.management.pattern", Property.JIRA_URL.getValue() + "/browse/%s");
        }
        if (Property.JIRA_RESULT_FIELDNAME.isSpecified()) {
            props.setProperty("Jira Result Field Name", Property.JIRA_RESULT_FIELDNAME.getValue());
        }
        if (Property.JIRA_RESULT_TRANSITION.isSpecified()) {
            props.setProperty("Jira Result Field Name", Property.JIRA_RESULT_TRANSITION.getValue());
        }
        if (System.getenv("BUILD_URL") != null) {
            props.setProperty("Jenkins build URL", System.getenv("BUILD_URL"));
        }

        // Now get all UI-specific properties
        if (Property.APP_PATH.isSpecified()) {
            props.setProperty("App Path", Property.APP_PATH.getValue());
        }
        if (Property.BROWSER.isSpecified()) {
            props.setProperty("Browser", Property.BROWSER.getValue());
        }
        if (Property.BROWSER_VERSION.isSpecified()) {
            props.setProperty("Browser Version", Property.BROWSER_VERSION.getValue());
        }
        if (Property.DEVICE.isSpecified()) {
            props.setProperty("Device Name", Property.DEVICE.getValue());
        }
        if (Property.GRID_URL.isSpecified()) {
            props.setProperty("Grid URL", Property.GRID_URL.getValue());
        }
        if (Property.PLATFORM.isSpecified()) {
            props.setProperty("Platform", Property.PLATFORM.getValue());
        }
        if (Property.PLATFORM_VERSION.isSpecified()) {
            props.setProperty("Platform Version", Property.PLATFORM_VERSION.getValue());
        }
        if (BaseTest.userAgent != null) {
            props.setProperty("UserAgent", BaseTest.userAgent);
        }

        return props;
    }

}
