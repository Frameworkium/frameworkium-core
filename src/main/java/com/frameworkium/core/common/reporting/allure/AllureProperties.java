package com.frameworkium.core.common.reporting.allure;

import com.frameworkium.core.common.properties.CommonProperty;
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
            props.putAll(com.frameworkium.core.api.reporting.allure.AllureProperties.getApiProperties());
            props.putAll(com.frameworkium.core.ui.reporting.allure.AllureProperties.getUIProperties());

            props.store(fos, "See https://github.com/allure-framework/allure-core/wiki/Environment");

            fos.close();
        } catch (IOException e) {
            logger.error("IO problem when writing allure properties file", e);
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }


    public static Properties getCommonProps() {
        Properties props = new Properties();

        if (CommonProperty.BUILD.isSpecified()) {
            props.setProperty("Build", CommonProperty.BUILD.getValue());
        }
        if (CommonProperty.RESULT_VERSION.isSpecified()) {
            props.setProperty("Jira Result Version", CommonProperty.RESULT_VERSION.getValue());
        }
        if (CommonProperty.JIRA_URL.isSpecified()) {
            props.setProperty("allure.issues.tracker.pattern", CommonProperty.JIRA_URL.getValue() + "/browse/%s");
            props.setProperty("allure.tests.management.pattern", CommonProperty.JIRA_URL.getValue() + "/browse/%s");
        }
        if (CommonProperty.JIRA_RESULT_FIELDNAME.isSpecified()) {
            props.setProperty("Jira Result Field Name", CommonProperty.JIRA_RESULT_FIELDNAME.getValue());
        }
        if (CommonProperty.JIRA_RESULT_TRANSITION.isSpecified()) {
            props.setProperty("Jira Result Field Name", CommonProperty.JIRA_RESULT_TRANSITION.getValue());
        }
        if(System.getenv("BUILD_URL") != null) {
            props.setProperty("Jenkins build URL", System.getenv("BUILD_URL"));
        }
        return props;
    }

}
