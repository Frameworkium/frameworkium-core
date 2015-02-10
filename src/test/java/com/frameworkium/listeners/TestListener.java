package com.frameworkium.listeners;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.SkipException;

import com.frameworkium.annotations.Jira;
import com.frameworkium.jira.Execution;
import com.jayway.restassured.internal.http.Status;
public class TestListener implements ITestListener {

    static final Logger logger = LogManager.getLogger();

    @Override
    public void onTestStart(ITestResult result) {
        logger.info(String.format("starting %s.%s", result.getTestClass()
                .getName(), result.getMethod().getMethodName()));
        
    
		String jiraId = result.getMethod().getConstructorOrMethod().getMethod()
				.getAnnotation(Jira.class).value();	

		new Execution("AppUnderTest SMOKE", jiraId)
				.update(Execution.STATUS.ZAPI_STATUS_WIP, "log" , null);
        
    }

    @Override
    public void onTestSuccess(ITestResult result) {
    	        
    	logger.info(String.format("PASS %s.%s",
                result.getTestClass().getName(), result.getMethod()
                        .getMethodName()));
    	
		String jiraId = result.getMethod().getConstructorOrMethod().getMethod()
				.getAnnotation(Jira.class).value();	

		new Execution("Konakart SMOKE", jiraId)
				.update(Execution.STATUS.ZAPI_STATUS_PASS, "log" , null);
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
        
		String jiraId = result.getMethod().getConstructorOrMethod().getMethod()
				.getAnnotation(Jira.class).value();	

		new Execution("Konakart SMOKE", jiraId)
				.update(Execution.STATUS.ZAPI_STATUS_FAIL, "log" , null);
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
        
		String jiraId = result.getMethod().getConstructorOrMethod().getMethod()
				.getAnnotation(Jira.class).value();	

		new Execution("Konakart SMOKE", jiraId)
				.update(Execution.STATUS.ZAPI_STATUS_BLOCKED, "log" , null);
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
