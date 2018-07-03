package com.frameworkium.core.common.listeners;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.common.reporting.TestIdUtils;
import com.frameworkium.core.common.reporting.jira.JiraConfig;
import com.frameworkium.core.common.reporting.jira.api.JiraTest;
import com.frameworkium.core.common.reporting.jira.zapi.Execution;
import com.frameworkium.core.common.reporting.spira.SpiraExecution;
import com.frameworkium.core.ui.capture.ScreenshotCapture;
import com.frameworkium.core.ui.tests.BaseUITest;
import com.google.common.base.Throwables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;

import java.lang.reflect.Method;

import static com.frameworkium.core.common.properties.Property.CAPTURE_URL;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.time.DateUtils.MILLIS_PER_SECOND;

public class ResultLoggerListener implements ITestListener {

    private final Logger logger = LogManager.getLogger();

    @Override
    public void onTestStart(ITestResult result) {

        String issueOrTestCaseId = getIssueOrTestCaseIdAnnotation(result);
        if (issueOrTestCaseId.isEmpty()) {
            return;
        }

        String comment = String.format(
                "Starting %s.%s",
                result.getTestClass().getName(),
                result.getMethod().getMethodName());

        if (zapiLoggingParamsProvided(result)) {
            logger.info("Logging WIP to zapi");
            new Execution(issueOrTestCaseId)
                    .update(JiraConfig.ZapiStatus.ZAPI_STATUS_WIP, comment);
        }
        if (jiraTransitionLoggingParamsProvided(result)) {
            logger.info("Logging WIP to Jira using issue transitions");
            moveThroughTransitions(issueOrTestCaseId,
                    JiraConfig.JiraTransition.JIRA_TRANSITION_WIP);
            JiraTest.addComment(issueOrTestCaseId, comment);
        }
        if (jiraFieldLoggingParamsProvided(result)) {
            logger.info("Logging WIP to Jira by updating the specified field - "
                    + Property.JIRA_RESULT_FIELD_NAME.getValue());
            JiraTest.changeIssueFieldValue(
                    issueOrTestCaseId,
                    Property.JIRA_RESULT_FIELD_NAME.getValue(),
                    JiraConfig.JiraFieldStatus.JIRA_STATUS_WIP);
            JiraTest.addComment(issueOrTestCaseId, comment);
        }
    }

    private void moveThroughTransitions(String issueAnnotation, String[] jiraTransitions) {
        for (String jiraTransition : jiraTransitions) {
            try {
                JiraTest.transitionIssue(issueAnnotation, jiraTransition);
                logger.debug(
                        "Performed transition '{}' on '{}'",
                        jiraTransition,
                        issueAnnotation);
            } catch (Exception e) {
                logger.error(
                        "Failed to perform transition '{}' on '{}' - maybe not possible given the state?",
                        jiraTransition,
                        issueAnnotation);
            }
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {

        String issueOrTestCaseId = getIssueOrTestCaseIdAnnotation(result);
        if (issueOrTestCaseId.isEmpty()) {
            return;
        }

        String comment = "PASS\n" + baseComment(result);

        if (zapiLoggingParamsProvided(result)) {
            logger.info("Logging PASS to zapi");
            new Execution(issueOrTestCaseId)
                    .update(JiraConfig.ZapiStatus.ZAPI_STATUS_PASS, comment);
        }
        if (jiraTransitionLoggingParamsProvided(result)) {
            logger.info("Logging PASS to Jira using issue transitions");
            moveThroughTransitions(issueOrTestCaseId,
                    JiraConfig.JiraTransition.JIRA_TRANSITION_PASS);
            JiraTest.addComment(issueOrTestCaseId, comment);
        }
        if (jiraFieldLoggingParamsProvided(result)) {
            logger.info("Logging PASS to Jira by updating the specified field - "
                    + Property.JIRA_RESULT_FIELD_NAME.getValue());
            JiraTest.changeIssueFieldValue(
                    issueOrTestCaseId,
                    Property.JIRA_RESULT_FIELD_NAME.getValue(),
                    JiraConfig.JiraFieldStatus.JIRA_STATUS_PASS);
            JiraTest.addComment(issueOrTestCaseId, comment);
        }
        if (spiraLoggingParamsProvided(result)) {
            new SpiraExecution().recordTestResult(
                    issueOrTestCaseId,
                    JiraConfig.SpiraStatus.SPIRA_STATUS_PASS,
                    comment,
                    result);
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {

        if (result.getThrowable() instanceof AssertionError) {
            markAsFailed(result);
        } else {
            markAsBlocked(result);
        }
    }

    private void markAsFailed(ITestResult result) {

        String issueOrTestCaseId = getIssueOrTestCaseIdAnnotation(result);
        if (issueOrTestCaseId.isEmpty()) {
            return;
        }

        String comment = "FAIL\n" + this.baseComment(result);

        if (zapiLoggingParamsProvided(result)) {
            logger.info("Logging FAIL to zapi");
            new Execution(issueOrTestCaseId)
                    .update(JiraConfig.ZapiStatus.ZAPI_STATUS_FAIL, comment);
        }
        if (jiraTransitionLoggingParamsProvided(result)) {
            logger.info("Logging FAIL to Jira using issue transitions");
            moveThroughTransitions(issueOrTestCaseId,
                    JiraConfig.JiraTransition.JIRA_TRANSITION_FAIL);
            JiraTest.addComment(issueOrTestCaseId, comment);
        }
        if (jiraFieldLoggingParamsProvided(result)) {
            logger.info("Logging FAIL to Jira by updating the specified field - "
                    + Property.JIRA_RESULT_FIELD_NAME.getValue());
            JiraTest.changeIssueFieldValue(
                    issueOrTestCaseId,
                    Property.JIRA_RESULT_FIELD_NAME.getValue(),
                    JiraConfig.JiraFieldStatus.JIRA_STATUS_FAIL);
            JiraTest.addComment(issueOrTestCaseId, comment);
        }
        if (spiraLoggingParamsProvided(result)) {
            new SpiraExecution().recordTestResult(
                    issueOrTestCaseId,
                    JiraConfig.SpiraStatus.SPIRA_STATUS_FAIL,
                    comment,
                    result);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        markAsBlocked(result);
    }

    private void markAsBlocked(ITestResult result) {

        String issueOrTestCaseId = getIssueOrTestCaseIdAnnotation(result);
        if (issueOrTestCaseId.isEmpty()) {
            return;
        }

        String comment = "BLOCKED\n" + this.baseComment(result);

        if (zapiLoggingParamsProvided(result)) {
            logger.info("Logging BLOCKED to zapi");
            new Execution(issueOrTestCaseId)
                    .update(JiraConfig.ZapiStatus.ZAPI_STATUS_BLOCKED, comment);
        }
        if (jiraTransitionLoggingParamsProvided(result)) {
            logger.info("Logging BLOCKED to Jira using issue transitions");
            moveThroughTransitions(issueOrTestCaseId,
                    JiraConfig.JiraTransition.JIRA_TRANSITION_BLOCKED);
            JiraTest.addComment(issueOrTestCaseId, comment);
        }
        if (jiraFieldLoggingParamsProvided(result)) {
            logger.info("Logging BLOCKED to Jira by updating the specified field - "
                    + Property.JIRA_RESULT_FIELD_NAME.getValue());
            JiraTest.changeIssueFieldValue(
                    issueOrTestCaseId,
                    Property.JIRA_RESULT_FIELD_NAME.getValue(),
                    JiraConfig.JiraFieldStatus.JIRA_STATUS_BLOCKED);
            JiraTest.addComment(issueOrTestCaseId, comment);
        }
        if (spiraLoggingParamsProvided(result)) {
            new SpiraExecution().recordTestResult(
                    issueOrTestCaseId,
                    JiraConfig.SpiraStatus.SPIRA_STATUS_BLOCKED,
                    comment,
                    result);
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}

    @Override
    public void onStart(ITestContext context) {}

    @Override
    public void onFinish(ITestContext context) {}

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
                && Property.JIRA_RESULT_FIELD_NAME.isSpecified()
                && !getIssueOrTestCaseIdAnnotation(result).isEmpty();
    }

    private Boolean spiraLoggingParamsProvided(ITestResult result) {
        return Property.SPIRA_URL.isSpecified()
                && !getIssueOrTestCaseIdAnnotation(result).isEmpty();
    }

    /**
     * If neither are specified then the empty string will be returned.
     * {@see TestIdUtils#getIssueOrTestCaseIdValue(Method)}
     *
     * @return the value of either the @Issue or @TestCaseId annotation for the provided test result.
     */
    private String getIssueOrTestCaseIdAnnotation(ITestResult result) {
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        return TestIdUtils.getIssueOrTmsLinkValue(method).orElse("");
    }

    private String getOSInfo() {
        return String.format(
                "%s - %s (%s)",
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("os.arch"));
    }

    private String baseComment(ITestResult result) {

        StringBuilder commentBuilder = new StringBuilder();

        commentBuilder.append("Test: ")
                .append(result.getTestClass().getName())
                .append(".")
                .append(result.getMethod().getMethodName())
                .append("\nDuration: ")
                .append(((result.getEndMillis() - result.getStartMillis()) / MILLIS_PER_SECOND))
                .append("seconds");

        if (!isNull(System.getenv("BUILD_URL"))) {
            commentBuilder.append("Jenkins build: ")
                    .append(System.getenv("BUILD_URL"));
        }
        if (ScreenshotCapture.isRequired()) {
            commentBuilder.append("Capture API: ")
                    .append(CAPTURE_URL.getValue());
        }
        commentBuilder.append("\nOS: ")
                .append(getOSInfo())
                .append("\nUserAgent: ")
                .append(BaseUITest.getUserAgent());

        if (!isNull(result.getThrowable())) {
            commentBuilder.append("\nStacktrace: ")
                    .append(Throwables.getStackTraceAsString(result.getThrowable()));
        }

        return commentBuilder.toString();
    }
}
