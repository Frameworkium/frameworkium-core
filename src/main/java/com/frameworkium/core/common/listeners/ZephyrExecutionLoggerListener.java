package com.frameworkium.core.common.listeners;

import com.frameworkium.core.JiraModule;
import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.common.reporting.TestIdUtils;
import com.frameworkium.core.common.reporting.jira.JiraConfig;
import com.frameworkium.core.common.reporting.jira.dto.executionsearch.ExecutionSearchListDto;
import com.frameworkium.core.common.reporting.jira.util.ExecutionUtil;
import com.frameworkium.core.common.reporting.jira.zapi.*;
import com.frameworkium.core.ui.UITestLifecycle;
import com.frameworkium.core.ui.capture.ScreenshotCapture;
import com.google.common.base.Throwables;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;

import java.lang.reflect.Method;
import java.util.List;

import javax.inject.Inject;

import static com.frameworkium.core.common.properties.Property.CAPTURE_URL;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.time.DateUtils.MILLIS_PER_SECOND;

public class ZephyrExecutionLoggerListener implements ITestListener {
    private final Logger logger = LogManager.getLogger();
    //@Inject
    private ExecutionExtraFactory executionExtraFactory;
    //@Inject
    private ExecutionSearch executionSearch;

    @Inject
    public ZephyrExecutionLoggerListener(final ExecutionExtraFactory executionExtraFactory, final ExecutionSearch executionSearch) {
        this.executionExtraFactory = executionExtraFactory;
        this.executionSearch = executionSearch;
    }

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

        //if (zapiLoggingParamsProvided(result)) {
        logger.info("Logging WIP to zapi");
        //given the issue and fixVersion there may be multiple executions to update
        final ExecutionSearchListDto executionSearchListDto = executionSearch.search(String.format("issue='%s' and fixVersion='%s'", issueOrTestCaseId, "version"));

        final List<ExecutionExtra> executionExtras = executionSearchListDto.executions.stream()
                .filter(e -> e.executionLightDto.cycleName.equals(Property.ZAPI_CYCLE_REGEX.getValue()))
                .map(e -> e.executionLightDto.id.intValue())
                .map(executionExtraFactory::create)
                .collect(toList());

        executionExtras.forEach(executionExtra -> executionExtra.updateComment(comment));
        executionExtras.forEach(executionExtra -> executionExtra.updateStatus(JiraConfig.ZapiStatus.ZAPI_STATUS_WIP));
        //}
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
            new ExecutionUtil(issueOrTestCaseId)
                    .update(JiraConfig.ZapiStatus.ZAPI_STATUS_PASS, comment);
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
            new ExecutionUtil(issueOrTestCaseId)
                    .update(JiraConfig.ZapiStatus.ZAPI_STATUS_FAIL, comment);
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
            new ExecutionUtil(issueOrTestCaseId)
                    .update(JiraConfig.ZapiStatus.ZAPI_STATUS_BLOCKED, comment);
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}

    @Override
    public void onStart(ITestContext context) {
        final Injector parentInjector = context.getSuite().getParentInjector();
        final List<Module> guiceModules = context.getGuiceModules(JiraModule.class);
        parentInjector.createChildInjector(guiceModules).injectMembers(this);
    }

    @Override
    public void onFinish(ITestContext context) {}

    private boolean zapiLoggingParamsProvided(ITestResult result) {
        return Property.JIRA_URL.isSpecified()
                && Property.RESULT_VERSION.isSpecified()
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
