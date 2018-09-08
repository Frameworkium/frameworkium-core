package com.frameworkium.core.common.reporting.allure;

import com.frameworkium.core.ui.tests.BaseUITest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static com.frameworkium.core.common.properties.Property.*;
import static java.util.Objects.isNull;

public class AllureProperties {

    private static final Logger logger = LogManager.getLogger();

    private AllureProperties() {
        // hide default constructor for this util class
    }

    /**
     * Creates the Allure environment.properties file based on properties used.
     */
    public static void create() {
        try (FileOutputStream fos = new FileOutputStream("target/allure-results/environment.properties")) {
            Properties props = new Properties();
            props.putAll(getCommonProps());
            props.store(fos, "See https://github.com/allure-framework/allure-core/wiki/Environment");
        } catch (IOException e) {
            logger.error("IO problem when writing allure properties file", e);
        }
    }

    private static Properties getCommonProps() {
        Properties props = new Properties();

        if (BUILD.isSpecified()) {
            props.setProperty("Build", BUILD.getValue());
        }
        if (!isNull(System.getenv("BUILD_URL"))) {
            props.setProperty("Jenkins build URL", System.getenv("BUILD_URL"));
        }
        if (!isNull(System.getProperty("threads"))) {
            props.setProperty("Test Thread Count", System.getProperty("threads"));
        }
        if (!isNull(System.getProperty("config"))) {
            props.setProperty("Config file", System.getProperty("config"));
        }

        setJiraProperties(props);
        setUIProperties(props);

        return props;
    }

    private static void setJiraProperties(Properties props) {
        if (RESULT_VERSION.isSpecified()) {
            props.setProperty("Jira Result Version", RESULT_VERSION.getValue());
        }
        if (JIRA_URL.isSpecified()) {
            final String jiraPattern = JIRA_URL.getValue() + "/browse/%s";
            props.setProperty("allure.issues.tracker.pattern", jiraPattern);
            props.setProperty("allure.tests.management.pattern", jiraPattern);
        }
        if (JIRA_RESULT_FIELD_NAME.isSpecified()) {
            props.setProperty("Jira Result Field Name", JIRA_RESULT_FIELD_NAME.getValue());
        }
        if (JIRA_RESULT_TRANSITION.isSpecified()) {
            props.setProperty("Jira Result Field Name", JIRA_RESULT_TRANSITION.getValue());
        }
    }

    private static void setUIProperties(Properties props) {
        if (APP_PATH.isSpecified()) {
            props.setProperty("App Path", APP_PATH.getValue());
        }
        if (DEVICE.isSpecified()) {
            props.setProperty("Device Name", DEVICE.getValue());
        }
        if (PLATFORM.isSpecified()) {
            props.setProperty("Platform", PLATFORM.getValue());
        }
        if (PLATFORM_VERSION.isSpecified()) {
            props.setProperty("Platform Version", PLATFORM_VERSION.getValue());
        }
        setBrowserProperties(props);
    }

    private static void setBrowserProperties(Properties props) {
        if (BROWSER.isSpecified()) {
            props.setProperty("Browser", BROWSER.getValue());
        }
        if (BROWSER_VERSION.isSpecified()) {
            props.setProperty("Browser Version", BROWSER_VERSION.getValue());
        }
        if (GRID_URL.isSpecified()) {
            props.setProperty("Grid URL", GRID_URL.getValue());
        }
        if (BaseUITest.getUserAgent().isPresent()) {
            props.setProperty("UserAgent", BaseUITest.getUserAgent().get());
        }
    }
}
