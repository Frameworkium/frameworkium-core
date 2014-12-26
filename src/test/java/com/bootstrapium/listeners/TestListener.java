package com.bootstrapium.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener{

    static final Logger logger = LogManager.getLogger();
    
    @Override
    public void onTestStart(ITestResult result) {
        logger.info(String.format("starting %s.%s",result.getTestClass().getName(), result.getMethod().getMethodName()));     
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info(String.format("PASS %s.%s",result.getTestClass().getName(), result.getMethod().getMethodName()));     
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.info(String.format("FAIL %s.%s",result.getTestClass().getName(), result.getMethod().getMethodName()));     
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.info(String.format("SKIP %s.%s",result.getTestClass().getName(), result.getMethod().getMethodName()));        
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
