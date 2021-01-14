package com.frameworkium.core.common.listeners;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.common.reporting.TestIdUtils;
import com.frameworkium.core.common.reporting.jira.JiraConfig;
import com.frameworkium.core.common.reporting.jira.service.Issue;
import com.frameworkium.core.common.reporting.jira.zapi.Execution;
import com.frameworkium.core.common.reporting.spira.SpiraExecution;
import com.frameworkium.core.ui.UITestLifecycle;
import com.frameworkium.core.ui.capture.ScreenshotCapture;
import com.google.common.base.Throwables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;

import java.lang.reflect.Method;

import static com.frameworkium.core.common.properties.Property.CAPTURE_URL;
import static java.text.MessageFormat.format;
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
        Issue issue = new Issue(issueOrTestCaseId);

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
            issue.addComment(comment);
        }
        if (jiraFieldLoggingParamsProvided(result)) {
            logger.info(() ->
                    format("Logging WIP to Jira by updating the specified field - {0}",
                            Property.JIRA_RESULT_FIELD_NAME.getValue()));
            issue.editField(
                    Property.JIRA_RESULT_FIELD_NAME.getValue(),
                    JiraConfig.JiraFieldStatus.JIRA_STATUS_WIP
            );
            issue.addComment(comment);
        }
    }

    private void moveThroughTransitions(String issueAnnotation, String[] jiraTransitions) {
        Issue issue = new Issue(issueAnnotation);
        for (String jiraTransition : jiraTransitions) {
            try {
                issue.transition(jiraTransition);
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
        Issue issue = new Issue(issueOrTestCaseId);

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
            issue.addComment(comment);
        }
        if (jiraFieldLoggingParamsProvided(result)) {
            logger.info(() ->
                    format("Logging PASS to Jira by updating the specified field - {0}",
                            Property.JIRA_RESULT_FIELD_NAME.getValue()));
            issue.editField(
                    Property.JIRA_RESULT_FIELD_NAME.getValue(),
                    JiraConfig.JiraFieldStatus.JIRA_STATUS_PASS);
            issue.addComment(comment);
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
        Issue issue = new Issue(issueOrTestCaseId);

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
            issue.addComment(comment);
        }
        if (jiraFieldLoggingParamsProvided(result)) {
            logger.info(() -> format("Logging FAIL to Jira by updating the specified field - {0}",
                    Property.JIRA_RESULT_FIELD_NAME.getValue()));
            issue.editField(
                    Property.JIRA_RESULT_FIELD_NAME.getValue(),
                    JiraConfig.JiraFieldStatus.JIRA_STATUS_FAIL);
            issue.addComment(comment);
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
        Issue issue = new Issue(issueOrTestCaseId);

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
            issue.addComment(comment);
        }
        if (jiraFieldLoggingParamsProvided(result)) {
            logger.info(() -> format("Logging BLOCKED to Jira by updating the specified field - {0}",
                    Property.JIRA_RESULT_FIELD_NAME.getValue()));
            issue.editField(
                    Property.JIRA_RESULT_FIELD_NAME.getValue(),
                    JiraConfig.JiraFieldStatus.JIRA_STATUS_BLOCKED);
            issue.addComment(comment);
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

    private boolean zapiLoggingParamsProvided(ITestResult result) {
        return Property.JIRA_URL.isSpecified()
                && Property.RESULT_VERSION.isSpecified()
                && !getIssueOrTestCaseIdAnnotation(result).isEmpty();
    }

    private boolean jiraTransitionLoggingParamsProvided(ITestResult result) {
        return Property.JIRA_URL.isSpecified()
                && Property.JIRA_RESULT_TRANSITION.isSpecified()
                && !getIssueOrTestCaseIdAnnotation(result).isEmpty();
    }

    private boolean jiraFieldLoggingParamsProvided(ITestResult result) {
        return Property.JIRA_URL.isSpecified()
                && Property.JIRA_RESULT_FIELD_NAME.isSpecified()
                && !getIssueOrTestCaseIdAnnotation(result).isEmpty();
    }

    private boolean spiraLoggingParamsProvided(ITestResult result) {
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
                .append(UITestLifecycle.get().getUserAgent());

        if (!isNull(result.getThrowable())) {
            commentBuilder.append("\nStacktrace: ")
                    .append(Throwables.getStackTraceAsString(result.getThrowable()));
        }

        return commentBuilder.toString();
    }
}
