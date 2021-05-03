package com.frameworkium.core.ui.capture.model.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.frameworkium.core.ui.capture.model.Browser;
import com.frameworkium.core.ui.capture.model.SoftwareUnderTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CreateExecution {

  private static final Logger logger = LogManager.getLogger();
  public String testID;
  public Browser browser;
  public SoftwareUnderTest softwareUnderTest;
  public String nodeAddress;

  /**
   * Create Capture execution.
   */
  public CreateExecution(String testID, String nodeAddress) {

    logger.debug("CreateExecution: testID='{}', nodeAddress='{}", testID, nodeAddress);
    this.testID = testID;
    this.browser = new Browser();
    this.softwareUnderTest = new SoftwareUnderTest();
    this.nodeAddress = nodeAddress;
  }
}
