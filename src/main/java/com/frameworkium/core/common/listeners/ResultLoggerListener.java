package com.frameworkium.core.common.listeners;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.common.reporting.jira.Config;
import com.frameworkium.core.common.reporting.jira.api.Test;
import com.frameworkium.core.common.reporting.jira.zapi.Execution;
import com.frameworkium.core.common.reporting.spira.SpiraExecution;
import com.frameworkium.core.ui.tests.BaseTest;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.TestCaseId;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

public class ResultLoggerListener implements ITestListener {

    private Logger logger = LogManager.getLogger(ResultLoggerListener.class);

    @Override
    public void onTestStart(ITestResult result) {

        if (!getIssueOrTestCaseIdAnnotation(result).isEmpty()) {

            String comment = String.format(
                    "Starting %s.%s",
                    result.getTestClass().getName(), result.getMethod().getMethodName());

            if (zapiLoggingParamsProvided(result)) {
                logger.info("Logging WIP to zapi");
                new Execution(getIssueOrTestCaseIdAnnotation(result))
                        .update(Config.ZAPI_STATUS.ZAPI_STATUS_WIP, comment, null);
            }
            if (jiraTransitionLoggingParamsProvided(result)) {
                logger.info("Logging WIP to Jira using issue transitions");
                moveThroughTransitions(getIssueOrTestCaseIdAnnotation(result),
                        Config.JIRA_TRANSITION.JIRA_TRANSITION_WIP);
                Test.addComment(getIssueOrTestCaseIdAnnotation(result), comment);
            }
            if (jiraFieldLoggingParamsProvided(result)) {
                logger.info("Logging WIP to jira by updating the specified field - "
                        + Property.JIRA_RESULT_FIELDNAME.getValue());
                Test.changeIssueFieldValue(
                        getIssueOrTestCaseIdAnnotation(result),
                        Property.JIRA_RESULT_FIELDNAME.getValue(),
                        Config.JIRA_FIELD_STATUS.JIRA_STATUS_WIP);
                Test.addComment(getIssueOrTestCaseIdAnnotation(result), comment);
            }
        }
    }

    private void moveThroughTransitions(String issueAnnotation, String[] jiraTransitions) {
        for (String jiraTransition : jiraTransitions) {
            try {
                Test.transitionIssue(issueAnnotation, jiraTransition);
                logger.debug(
                        "Performed transition '{}' on '{}'",
                        jiraTransition,
                        issueAnnotation);
            } catch (Exception e) {
                logger.warn(
                        "Failed to perform transition '{}' on '{}'- maybe not possible given the state?",
                        jiraTransition,
                        issueAnnotation);
            }
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {

        if (!getIssueOrTestCaseIdAnnotation(result).isEmpty()) {

            String comment = "PASS" + System.lineSeparator() + this.baseComment(result);

            if (zapiLoggingParamsProvided(result)) {
                logger.info("Logging PASS to zapi");
                new Execution(getIssueOrTestCaseIdAnnotation(result))
                        .update(Config.ZAPI_STATUS.ZAPI_STATUS_PASS, comment, null);
            }
            if (jiraTransitionLoggingParamsProvided(result)) {
                logger.info("Logging PASS to Jira using issue transitions");
                moveThroughTransitions(getIssueOrTestCaseIdAnnotation(result),
                        Config.JIRA_TRANSITION.JIRA_TRANSITION_PASS);
                Test.addComment(getIssueOrTestCaseIdAnnotation(result), comment);
            }
            if (jiraFieldLoggingParamsProvided(result)) {
                logger.info("Logging PASS to jira by updating the specified field - "
                        + Property.JIRA_RESULT_FIELDNAME.getValue());
                Test.changeIssueFieldValue(
                        getIssueOrTestCaseIdAnnotation(result),
                        Property.JIRA_RESULT_FIELDNAME.getValue(),
                        Config.JIRA_FIELD_STATUS.JIRA_STATUS_PASS);
                Test.addComment(getIssueOrTestCaseIdAnnotation(result), comment);
            }
            if (spiraLoggingParamsProvided(result)) {
                new SpiraExecution().recordTestResult(
                        getIssueOrTestCaseIdAnnotation(result),
                        Config.SPIRA_STATUS.SPIRA_STATUS_PASS,
                        comment,
                        result);
            }
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {

        if (!(result.getThrowable() instanceof AssertionError)
                && Config.FailTestOnlyIfAssertionError) {
            markAsBlocked(result);
        } else if (!getIssueOrTestCaseIdAnnotation(result).isEmpty()) {

            String comment = "FAIL" + System.lineSeparator() + this.baseComment(result);

            if (zapiLoggingParamsProvided(result)) {
                logger.info("Logging FAIL to zapi");
                new Execution(getIssueOrTestCaseIdAnnotation(result))
                        .update(Config.ZAPI_STATUS.ZAPI_STATUS_FAIL, comment, null);
            }
            if (jiraTransitionLoggingParamsProvided(result)) {
                logger.info("Logging FAIL to Jira using issue transitions");
                moveThroughTransitions(getIssueOrTestCaseIdAnnotation(result),
                        Config.JIRA_TRANSITION.JIRA_TRANSITION_FAIL);
                Test.addComment(getIssueOrTestCaseIdAnnotation(result), comment);
            }
            if (jiraFieldLoggingParamsProvided(result)) {
                logger.info("Logging FAIL to jira by updating the specified field - "
                        + Property.JIRA_RESULT_FIELDNAME.getValue());
                Test.changeIssueFieldValue(
                        getIssueOrTestCaseIdAnnotation(result),
                        Property.JIRA_RESULT_FIELDNAME.getValue(),
                        Config.JIRA_FIELD_STATUS.JIRA_STATUS_FAIL);
                Test.addComment(getIssueOrTestCaseIdAnnotation(result), comment);
            }
            if (spiraLoggingParamsProvided(result)) {
                new SpiraExecution().recordTestResult(
                        getIssueOrTestCaseIdAnnotation(result),
                        Config.SPIRA_STATUS.SPIRA_STATUS_FAIL,
                        comment,
                        result);
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        markAsBlocked(result);
    }

    private void markAsBlocked(ITestResult result) {

        if (!getIssueOrTestCaseIdAnnotation(result).isEmpty()) {

            String comment = "BLOCKED" + System.lineSeparator() + this.baseComment(result);

            if (zapiLoggingParamsProvided(result)) {
                logger.info("Logging BLOCKED to zapi");
                new Execution(getIssueOrTestCaseIdAnnotation(result))
                        .update(Config.ZAPI_STATUS.ZAPI_STATUS_BLOCKED, comment, null);
            }
            if (jiraTransitionLoggingParamsProvided(result)) {
                logger.info("Logging BLOCKED to Jira using issue transitions");
                moveThroughTransitions(getIssueOrTestCaseIdAnnotation(result),
                        Config.JIRA_TRANSITION.JIRA_TRANSITION_BLOCKED);
                Test.addComment(getIssueOrTestCaseIdAnnotation(result), comment);
            }
            if (jiraFieldLoggingParamsProvided(result)) {
                logger.info("Logging BLOCKED to jira by updating the specified field - "
                        + Property.JIRA_RESULT_FIELDNAME.getValue());
                Test.changeIssueFieldValue(
                        getIssueOrTestCaseIdAnnotation(result),
                        Property.JIRA_RESULT_FIELDNAME.getValue(),
                        Config.JIRA_FIELD_STATUS.JIRA_STATUS_BLOCKED);
                Test.addComment(getIssueOrTestCaseIdAnnotation(result), comment);
            }
            if (spiraLoggingParamsProvided(result)) {
                new SpiraExecution().recordTestResult(
                        getIssueOrTestCaseIdAnnotation(result),
                        Config.SPIRA_STATUS.SPIRA_STATUS_BLOCKED,
                        comment,
                        result);
            }
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

    private Boolean zapiLoggingParamsProvided(ITestResult result) {
        return Property.JIRA_URL.isSpecified()
                && Property.RESULT_VERSION.isSpecified()
                && !getIssueOrTestCaseIdAnnotation(result).isEmpty();
    }

    private Boolean jiraTransitionLoggingParamsProvided(ITestResult result) {
        return Property.JIRA_URL.isSpecified()
                && Property.JIRA_RESULT_TRANSITION.isSpecified()
                && !getIssueOrTestCaseIdAnnotation(result).isEmpty();
    }

    private Boolean jiraFieldLoggingParamsProvided(ITestResult result) {
        return Property.JIRA_URL.isSpecified()
                && Property.JIRA_RESULT_FIELDNAME.isSpecified()
                && !getIssueOrTestCaseIdAnnotation(result).isEmpty();
    }

    private Boolean spiraLoggingParamsProvided(ITestResult result) {
        return Property.SPIRA_URL.isSpecified()
                && !getIssueOrTestCaseIdAnnotation(result).isEmpty();
    }

    private String getIssueOrTestCaseIdAnnotation(ITestResult result) {
        String annotation = StringUtils.EMPTY;
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        try {
            annotation = method.getAnnotation(Issue.class).value();
        } catch (NullPointerException ignored) { /* ignored */ }
        try {
            annotation = method.getAnnotation(TestCaseId.class).value();
        } catch (NullPointerException ignored) { /* ignored */ }
        return annotation;
    }

    private String getOSInfo() {
        return String.format(
                "%s - %s (%s)",
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("os.arch"));
    }

    private String getStackTraceFromThrowable(Throwable cause) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        cause.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    private String baseComment(ITestResult result) {

        StringBuilder comment = new StringBuilder();

        comment.append(String.format(
                "Test: %s.%s",
                result.getTestClass().getName(),
                result.getMethod().getMethodName()))
                .append(System.lineSeparator())
                .append("Duration: ")
                .append(((result.getEndMillis() - result.getStartMillis()) / 1000))
                .append("seconds")
                .append(System.lineSeparator());

        if (System.getenv("BUILD_URL") != null) {
            comment.append("Jenkins build: ")
                    .append(System.getenv("BUILD_URL"))
                    .append(System.lineSeparator());
        }
        //TODO - fix this!
        //        if (CAPTURE_URL.isSpecified()) {
        //            comment.append("Capture API: ");
        //            comment.append(CAPTURE_URL.getValue());
        //            comment.append(System.lineSeparator());
        //        }
        comment.append("OS: ")
                .append(getOSInfo())
                .append(System.lineSeparator())
                .append("UserAgent: ")
                .append(BaseTest.userAgent);

        if (result.getThrowable() != null) {
            comment.append(System.lineSeparator())
                    .append("Stacktrace: ")
                    .append(getStackTraceFromThrowable(result.getThrowable()));
        }

        return comment.toString();
    }
}
