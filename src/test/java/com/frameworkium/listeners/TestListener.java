package com.frameworkium.listeners;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.SkipException;


public class TestListener implements ITestListener {

    static final Logger logger = LogManager.getLogger();

    @Override
    public void onTestStart(ITestResult result) {
        logger.info(String.format("starting %s.%s", result.getTestClass()
                .getName(), result.getMethod().getMethodName()));
    }

    @Override
    public void onTestSuccess(ITestResult result) {

    	logger.info(String.format("PASS %s.%s",
                result.getTestClass().getName(), result.getMethod()
                        .getMethodName()));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error(String.format("FAIL %s.%s", result.getTestClass()
                .getName(), result.getMethod().getMethodName()));
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Throwable cause = result.getThrowable();
        if (null != cause) {
            cause.printStackTrace(pw);
            logger.error(sw.getBuffer().toString());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.error(String.format("SKIP %s.%s", result.getTestClass()
                .getName(), result.getMethod().getMethodName()));
        Throwable cause = result.getThrowable();
        if (cause != null
                && SkipException.class.isAssignableFrom(cause
                        .getClass())) {
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

}
