package com.frameworkium.core.ui.listeners;

import com.frameworkium.core.ui.capture.ElementHighlighter;
import com.frameworkium.core.ui.capture.ScreenshotCapture;
import com.frameworkium.core.ui.capture.model.Command;
import com.frameworkium.core.ui.tests.BaseTest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.testng.*;

import static org.apache.commons.lang3.StringUtils.abbreviate;

/**
 * Assumes {@link ScreenshotCapture}.isRequired() is true for WebDriver events.
 */
public class CaptureListener implements WebDriverEventListener, ITestListener {

    private void takeScreenshotAndSend(Command command, WebDriver driver) {
        BaseTest.getCapture().takeAndSendScreenshot(command, driver);
    }

    private void takeScreenshotAndSend(String action, WebDriver driver) {
        Command command = new Command(action, "n/a", "n/a");
        takeScreenshotAndSend(command, driver);
    }

    private void takeScreenshotAndSend(String action, WebDriver driver, Throwable thrw) {

        BaseTest.getCapture().takeAndSendScreenshotWithError(
                new Command(action, "n/a", "n/a"),
                driver,
                thrw.getMessage() + "\n" + ExceptionUtils.getStackTrace(thrw));
    }

    private void sendFinalScreenshot(ITestResult result, String action) {
        // As this can be called from any test type, ensure this is not an API test
        if (ScreenshotCapture.isRequired() && !isFromApiTest(result)) {
            Throwable thrw = result.getThrowable();
            if (null != thrw) {
                takeScreenshotAndSend(action, BaseTest.getDriver(), thrw);
            } else {
                Command command = new Command(action, "n/a", "n/a");
                takeScreenshotAndSend(command, BaseTest.getDriver());
            }
        }
    }

    private boolean isFromApiTest(ITestResult result) {
        return result.getInstance()
                instanceof com.frameworkium.core.api.tests.BaseTest;
    }

    private void highlightElementAndSendScreenshot(
            String action, WebDriver driver, WebElement element) {

        ElementHighlighter highlighter = new ElementHighlighter(driver);
        highlighter.highlightElement(element);
        Command command = new Command(action, element);
        takeScreenshotAndSend(command, driver);
        highlighter.unhighlightPrevious();
    }

    /* WebDriver events */
    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
        highlightElementAndSendScreenshot("click", driver, element);
    }

    @Override
    public void afterChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysSent) {
        takeScreenshotAndSend("change", driver);
    }

    @Override
    public void beforeNavigateBack(WebDriver driver) {
        takeScreenshotAndSend("nav back", driver);
    }

    @Override
    public void beforeNavigateForward(WebDriver driver) {
        takeScreenshotAndSend("nav forward", driver);
    }

    @Override
    public void beforeNavigateTo(String url, WebDriver driver) {
        Command command = new Command("nav", "url", url);
        takeScreenshotAndSend(command, driver);
    }

    @Override
    public void beforeScript(String script, WebDriver driver) {
        // ignore scripts which are part of Frameworkium
        if (!isFrameworkiumScript(script)) {
            takeScreenshotAndSend(
                    new Command("script", "n/a", abbreviate(script, 42)),
                    driver);
        }
    }

    private boolean isFrameworkiumScript(String script) {
        String browserAgentScript = "return navigator.userAgent;";
        String angularCheckRegex = "return typeof (ng|angular) == 'object';";
        String forceVisiblePrefix = "arguments[0].style.zindex='10000';";
        String angularFrameworkContains = "angular.getTestability(el).whenStable(callback);";

        return script.equals(browserAgentScript)
                || script.matches(angularCheckRegex)
                || script.startsWith(forceVisiblePrefix)
                || script.contains(angularFrameworkContains);
    }

    /* Test end methods */

    @Override
    public void onTestSuccess(ITestResult result) {
        sendFinalScreenshot(result, "pass");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        sendFinalScreenshot(result, "fail");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        sendFinalScreenshot(result, "skip");
    }

    /* Methods we don't really want screenshots for. */

    @Override
    public void onException(Throwable thrw, WebDriver driver) {}

    @Override
    public void afterClickOn(WebElement element, WebDriver driver) {}

    @Override
    public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {}

    @Override
    public void afterFindBy(By by, WebElement arg1, WebDriver arg2) {}

    @Override
    public void afterNavigateBack(WebDriver driver) {}

    @Override
    public void afterNavigateForward(WebDriver driver) {}

    @Override
    public void beforeNavigateRefresh(WebDriver webDriver) {}

    @Override
    public void afterNavigateRefresh(WebDriver webDriver) {}

    @Override
    public void afterNavigateTo(String url, WebDriver driver) {}

    @Override
    public void afterScript(String script, WebDriver driver) {}

    @Override
    public void beforeFindBy(By by, WebElement element, WebDriver arg2) {}

    @Override
    public void onTestStart(ITestResult result) {}

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}

    @Override
    public void onStart(ITestContext context) {}

    @Override
    public void onFinish(ITestContext context) {}
}
