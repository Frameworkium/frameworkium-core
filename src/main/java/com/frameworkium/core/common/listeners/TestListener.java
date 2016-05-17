package com.frameworkium.core.common.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.SkipException;
import ru.yandex.qatools.allure.annotations.Step;

public class TestListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("starting %s.%s", result.getTestClass().getName(), result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("PASS %s.%s", result.getTestClass().getName(), result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("FAIL %s.%s", result.getTestClass().getName(), result.getMethod().getMethodName());
        Throwable cause = result.getThrowable();
        if (null != cause) {
            logger.error(cause.getMessage(), cause);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.error("SKIP %s.%s", result.getTestClass().getName(), result.getMethod().getMethodName());
        Throwable cause = result.getThrowable();
        if (cause != null && SkipException.class.isAssignableFrom(cause.getClass())) {
            logger.error(cause.getMessage());
        }
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
