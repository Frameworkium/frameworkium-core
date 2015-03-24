package com.frameworkium.reporting;

import static com.frameworkium.config.SystemProperty.APP_PATH;
import static com.frameworkium.config.SystemProperty.BROWSER;
import static com.frameworkium.config.SystemProperty.BROWSER_VERSION;
import static com.frameworkium.config.SystemProperty.BUILD;
import static com.frameworkium.config.SystemProperty.DEVICE_NAME;
import static com.frameworkium.config.SystemProperty.GRID_URL;
import static com.frameworkium.config.SystemProperty.JIRA_RESULT_VERSION;
import static com.frameworkium.config.SystemProperty.JIRA_URL;
import static com.frameworkium.config.SystemProperty.PLATFORM;
import static com.frameworkium.config.SystemProperty.PLATFORM_VERSION;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AllureProperties {
    private static final Logger logger = LogManager.getLogger(AllureProperties.class);

    public static void create() {
        FileOutputStream fos = null;
        try {
            Properties props = new Properties();
            fos = new FileOutputStream("target/allure-results/environment.properties");

            if (APP_PATH.isSpecified()) {
                props.setProperty("App Path", APP_PATH.getValue());
            }
            if (BROWSER.isSpecified()) {
                props.setProperty("Browser", BROWSER.getValue());
            }
            if (BROWSER_VERSION.isSpecified()) {
                props.setProperty("Browser Version", BROWSER_VERSION.getValue());
            }
            if (BUILD.isSpecified()) {
                props.setProperty("Build", BUILD.getValue());
            }
            if (DEVICE_NAME.isSpecified()) {
                props.setProperty("Device Name", DEVICE_NAME.getValue());
            }
            if (GRID_URL.isSpecified()) {
                props.setProperty("Grid URL", GRID_URL.getValue());
            }
            if (PLATFORM.isSpecified()) {
                props.setProperty("Platform", PLATFORM.getValue());
            }
            if (PLATFORM_VERSION.isSpecified()) {
                props.setProperty("Platform Version", PLATFORM_VERSION.getValue());
            }
            if (JIRA_RESULT_VERSION.isSpecified()) {
                props.setProperty("Jira Result Version", JIRA_RESULT_VERSION.getValue());
            }
            if (JIRA_URL.isSpecified()) {
                props.setProperty("allure.issues.tracker.pattern", JIRA_URL.getValue() + "/browse/%s");
            }

            props.store(fos, "See https://github.com/allure-framework/allure-core/wiki/Environment");

            fos.close();
        } catch (IOException e) {
            logger.error("IO problem when writing allure properties file", e);
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }
}
