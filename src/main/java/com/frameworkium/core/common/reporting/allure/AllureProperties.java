package com.frameworkium.core.common.reporting.allure;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.UITestLifecycle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
        props.putAll(getAllFrameworkiumProperties());
        props.putAll(getCommonProps());
        props.putAll(getUIProperties());
        save(props);
    }

    public static void createAPI() {
        Properties props = new Properties();
        props.putAll(getAllFrameworkiumProperties());
        props.putAll(getCommonProps());
        save(props);
    }

    private static Properties getAllFrameworkiumProperties() {
        Map<String, String> allProperties =
                Arrays.stream(Property.values())
                        .filter(Property::isSpecified)
                        .collect(Collectors.toMap(
                                Property::toString,
                                Property::getValue));

        Properties properties = new Properties();
        properties.putAll(allProperties);
        return properties;
    }

    private static Properties getCommonProps() {
        Properties props = new Properties();

        if (nonNull(System.getenv("BUILD_URL"))) {
            props.setProperty("CI build URL", System.getenv("BUILD_URL"));
        }
        if (nonNull(System.getProperty("config"))) {
            props.setProperty("Config file", System.getProperty("config"));
        }

        if (Property.JIRA_URL.isSpecified()) {
            final String jiraPattern = Property.JIRA_URL.getValue() + "/browse/%s";
            props.setProperty("allure.issues.tracker.pattern", jiraPattern);
            props.setProperty("allure.tests.management.pattern", jiraPattern);
        }

        return props;
    }

    private static Properties getUIProperties() {
        Properties props = new Properties();
        UITestLifecycle.get().getUserAgent()
                .ifPresent(ua -> props.setProperty("UserAgent", ua));
        return props;
    }

    private static void save(Properties props) {
        try (FileOutputStream fos = new FileOutputStream(
                "target/allure-results/environment.properties")) {
            props.store(fos,
                    "See https://github.com/allure-framework/allure-core/wiki/Environment");
        } catch (IOException e) {
            logger.error("IO problem when writing allure properties file", e);
        }
    }
}
