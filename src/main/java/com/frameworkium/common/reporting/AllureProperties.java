package com.frameworkium.common.reporting;

import com.frameworkium.ui.tests.internal.BaseTest;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static com.frameworkium.config.SystemProperty.*;

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
            if (DEVICE.isSpecified()) {
                props.setProperty("Device Name", DEVICE.getValue());
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
            if (RESULT_VERSION.isSpecified()) {
                props.setProperty("Jira Result Version", RESULT_VERSION.getValue());
            }
            if (JIRA_URL.isSpecified()) {
                props.setProperty("allure.issues.tracker.pattern", JIRA_URL.getValue() + "/browse/%s");
                props.setProperty("allure.tests.management.pattern", JIRA_URL.getValue() + "/browse/%s");
            }
            if (JIRA_RESULT_FIELDNAME.isSpecified()) {
                props.setProperty("Jira Result Field Name", JIRA_RESULT_FIELDNAME.getValue());
            }
            if (JIRA_RESULT_TRANSITION.isSpecified()) {
                props.setProperty("Jira Result Field Name", JIRA_RESULT_TRANSITION.getValue());
            }
            if(System.getenv("BUILD_URL") != null) {
                props.setProperty("Jenkins build URL", System.getenv("BUILD_URL"));
            }
            if(BaseTest.userAgent != null) {
                props.setProperty("UserAgent", BaseTest.userAgent);
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
