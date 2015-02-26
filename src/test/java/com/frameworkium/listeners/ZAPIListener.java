package com.frameworkium.listeners;

import static com.frameworkium.config.SystemProperty.JIRA_RESULT_VERSION;
import static com.frameworkium.config.SystemProperty.JIRA_URL;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang.StringUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import ru.yandex.qatools.allure.annotations.Issue;

import com.frameworkium.jira.Config;
import com.frameworkium.jira.zapi.Execution;

public class ZAPIListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        if (loggingParamsProvided(result)) {
            String comment = String.format("Starting %s.%s", result.getTestClass().getName(), result
                    .getMethod().getMethodName());

            new Execution(JIRA_RESULT_VERSION.getValue(), getIssueAnnotation(result)).update(
                    Config.ZAPI_STATUS.ZAPI_STATUS_WIP, comment, null);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (loggingParamsProvided(result)) {
            String comment = String.format("%s.%s Passed in %s seconds", result.getTestClass().getName(),
                    result.getMethod().getMethodName(),
                    ((result.getEndMillis() - result.getStartMillis()) / 1000));

            new Execution(JIRA_RESULT_VERSION.getValue(), getIssueAnnotation(result)).update(
                    Config.ZAPI_STATUS.ZAPI_STATUS_PASS, comment, null);
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (loggingParamsProvided(result)) {
            String comment = String.format("%s.%s Failed in %s seconds", result.getTestClass().getName(),
                    result.getMethod().getMethodName(),
                    ((result.getEndMillis() - result.getStartMillis()) / 1000));

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            Throwable cause = result.getThrowable();
            if (null != cause) {
                cause.printStackTrace(pw);
                String stacktrace = sw.getBuffer().toString();
                comment = comment + System.lineSeparator() + System.lineSeparator() + stacktrace;
            }

            if (!(result.getThrowable() instanceof AssertionError) && Config.FailTestOnlyIfAssertionError) {
                new Execution(JIRA_RESULT_VERSION.getValue(), getIssueAnnotation(result)).update(
                        Config.ZAPI_STATUS.ZAPI_STATUS_BLOCKED, comment, null);
            } else {
                new Execution(JIRA_RESULT_VERSION.getValue(), getIssueAnnotation(result)).update(
                        Config.ZAPI_STATUS.ZAPI_STATUS_FAIL, comment, null);
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (loggingParamsProvided(result)) {
            String comment = String.format("%s.%s Skipped in %s seconds", result.getTestClass().getName(),
                    result.getMethod().getMethodName(),
                    ((result.getEndMillis() - result.getStartMillis()) / 1000));

            new Execution(JIRA_RESULT_VERSION.getValue(), getIssueAnnotation(result)).update(
                    Config.ZAPI_STATUS.ZAPI_STATUS_BLOCKED, comment, null);
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

    private Boolean loggingParamsProvided(ITestResult result) {
        return JIRA_URL.isSpecified() && JIRA_RESULT_VERSION.isSpecified()
                && !getIssueAnnotation(result).isEmpty();
    }

    private String getIssueAnnotation(ITestResult result) {
        try {
            return result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Issue.class).value();
        } catch (NullPointerException e) {
            return StringUtils.EMPTY;
        }
    }

}
