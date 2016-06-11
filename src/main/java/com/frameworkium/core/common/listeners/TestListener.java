package com.frameworkium.core.common.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;
import ru.yandex.qatools.allure.annotations.Step;

public class TestListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestListener.class);

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
        Throwable cause = result.getThrowable();
        if (null != cause) {
            logger.error(cause.getMessage(), cause);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("SKIP  {}", getTestIdentifier(result));
        Throwable cause = result.getThrowable();
        if (cause != null && SkipException.class.isAssignableFrom(cause.getClass())) {
            logger.warn(cause.getMessage());
        }
    }

    private String getTestIdentifier(ITestResult result) {
        return String.format("%s.%s",
                result.getInstanceName(),
                result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    @Override
    public void onStart(ITestContext context) {
    }

    @Override
    public void onFinish(ITestContext context) {
    }

    @Step("{0}")
    private void stepLogger(String toLog) {
        logger.info(toLog);
    }

}
