package com.frameworkium.core.common.reporting.allure;

import com.frameworkium.core.ui.tests.BaseUITest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static com.frameworkium.core.common.properties.Property.*;
import static java.util.Objects.nonNull;

/**
 * Creates the Allure environment.properties file based on properties used.
 * Each method will overwrite the existing file.
 */
public class AllureProperties {

    private static final Logger logger = LogManager.getLogger();

    private AllureProperties() {
        // hide default constructor for this util class
    }

    public static void createUI() {
        Properties props = new Properties();
        props.putAll(getCommonProps());
        props.putAll(getJiraProperties());
        props.putAll(getUIProperties());
        save(props);
    }

    public static void createAPI() {
        Properties props = new Properties();
        props.putAll(getCommonProps());
        props.putAll(getJiraProperties());
        save(props);
    }

    private static void save(Properties props) {
        try (FileOutputStream fos = new FileOutputStream("target/allure-results/environment.properties")) {
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
        if (nonNull(System.getenv("BUILD_URL"))) {
            props.setProperty("Jenkins build URL", System.getenv("BUILD_URL"));
        }
        if (THREADS.isSpecified()) {
            props.setProperty("Test Thread Count",
                    String.valueOf(THREADS.getIntWithDefault(1)));
        }
        if (nonNull(System.getProperty("config"))) {
            props.setProperty("Config file", System.getProperty("config"));
        }

        return props;
    }

    private static Properties getJiraProperties() {
        Properties props = new Properties();

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

        return props;
    }

    private static Properties getUIProperties() {
        Properties props = new Properties();

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
        props.putAll(getBrowserProperties());

        return props;
    }

    private static Properties getBrowserProperties() {
        Properties props = new Properties();

        if (BROWSER.isSpecified()) {
            props.setProperty("Browser", BROWSER.getValue());
        }
        if (BROWSER_VERSION.isSpecified()) {
            props.setProperty("Browser Version", BROWSER_VERSION.getValue());
        }
        if (GRID_URL.isSpecified()) {
            props.setProperty("Grid URL", GRID_URL.getValue());
        }

        BaseUITest.getUserAgent()
                .ifPresent(ua -> props.setProperty("UserAgent", ua));

        return props;
    }
}
