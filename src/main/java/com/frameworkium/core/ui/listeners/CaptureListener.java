package com.frameworkium.core.ui.listeners;

import com.frameworkium.core.ui.capture.ElementHighlighter;
import com.frameworkium.core.ui.capture.model.Command;
import com.frameworkium.core.ui.tests.BaseTest;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.testng.*;

/**
 * Assumes {@link com.frameworkium.core.ui.capture.ScreenshotCapture}.isRequired()
 * is true.
 */
public class CaptureListener implements WebDriverEventListener, ITestListener {

    private static final Logger logger = LogManager.getLogger(CaptureListener.class);

    private void takeScreenshotAndSend(Command command, WebDriver driver) {
        BaseTest.getCapture().takeAndSendScreenshot(command, driver, null);
    }

    private void takeScreenshotAndSend(String action, WebDriver driver) {
        Command command = new Command(action, "n/a", "n/a");
        takeScreenshotAndSend(command, driver);
    }

    private void takeScreenshotAndSend(
            String action, WebDriver driver, Throwable thrw) {
        Command command = new Command(action, "n/a", "n/a");
        BaseTest.getCapture().takeAndSendScreenshot(
                command, driver,
                thrw.getMessage() + "\n" + ExceptionUtils.getStackTrace(thrw));
    }

    private void sendFinalScreenshot(ITestResult result, String action) {
        // As this can be called from any test type, ensure this is a UI test
        if (!(result.getInstance() instanceof BaseTest)) {
            return;
        }
        Throwable thrw = result.getThrowable();
        if (null != thrw) {
            takeScreenshotAndSend(action, BaseTest.getDriver(), thrw);
        } else {
            Command command = new Command(action, "n/a", "n/a");
            takeScreenshotAndSend(command, BaseTest.getDriver());
        }
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
    public void afterChangeValueOf(WebElement element, WebDriver driver) {
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
        if (!script.contains("navigator.userAgent")) {
            takeScreenshotAndSend("script", driver);
        }
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
    public void beforeChangeValueOf(WebElement element, WebDriver driver) {}

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
