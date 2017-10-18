package com.frameworkium.core.api.tests;

import com.frameworkium.core.common.listeners.*;
import com.frameworkium.core.common.reporting.allure.AllureProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Listeners;

@Listeners({MethodInterceptor.class,
        TestListener.class,
        ResultLoggerListener.class})
public abstract class BaseTest {

    protected final Logger logger = LogManager.getLogger(this);

    /** Creates the allure properties for the report, after the test run. */
    @AfterSuite(alwaysRun = true)
    public static void createAllureProperties() {
        AllureProperties.create();
    }

}
