package com.frameworkium.lite.common.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;

public class TestListener implements ITestListener {

    private final Logger logger = LogManager.getLogger();

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("START {}", getTestIdentifier(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("PASS  {}", getTestIdentifier(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("FAIL  {}", getTestIdentifier(result));
        var cause = result.getThrowable();
        if (cause != null) {
            logger.error(cause.getMessage(), cause);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("SKIP  {}", getTestIdentifier(result));
        var cause = result.getThrowable();
        if (cause != null && SkipException.class.isAssignableFrom(cause.getClass())) {
            logger.warn(cause.getMessage());
        }
    }

    private String getTestIdentifier(ITestResult result) {
        return String.format("%s.%s",
                result.getInstanceName(),
                result.getMethod().getMethodName());
    }
}
