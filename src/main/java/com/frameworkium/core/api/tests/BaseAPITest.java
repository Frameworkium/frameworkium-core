package com.frameworkium.core.api.tests;

import com.frameworkium.core.common.reporting.allure.AllureProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

// Uses the listeners in main.resources.META-INF.services.org.testng.ITestNGListener
@Test(groups = "base-api")
public abstract class BaseAPITest {

  protected final Logger logger = LogManager.getLogger(this);

  /**
   * Creates the allure properties for the report, after the test run.
   */
  @AfterSuite(alwaysRun = true)
  public static void createAllureProperties() {
    AllureProperties.createAPI();
  }

}
