package com.frameworkium.lite.ui.listeners;

import com.frameworkium.lite.ui.ExtraExpectedConditions;
import com.frameworkium.lite.ui.UITestLifecycle;
import com.frameworkium.lite.ui.browsers.UserAgent;
import com.frameworkium.lite.ui.capture.ElementHighlighter;
import com.frameworkium.lite.ui.capture.ScreenshotCapture;
import com.frameworkium.lite.ui.capture.model.Command;
import com.frameworkium.lite.ui.pages.Visibility;
import com.frameworkium.lite.ui.tests.BaseUITest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.testng.*;

import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.abbreviate;

/**
 * Assumes {@link ScreenshotCapture#isRequired()} is true for WebDriver events.
 */
public class CaptureListener implements WebDriverEventListener, ITestListener {

    private final Logger logger = LogManager.getLogger(this);

    private static final List<String> FRAMEWORKIUM_SCRIPTS  = Arrays.asList(
            UserAgent.SCRIPT,
            Visibility.FORCE_VISIBLE_SCRIPT,
            ExtraExpectedConditions.JQUERY_AJAX_DONE_SCRIPT
    );

    private void takeScreenshotAndSend(Command command, WebDriver driver) {
        try {
            UITestLifecycle.get().getCapture().takeAndSendScreenshot(command, driver);
        } catch (NullPointerException npe) {
            logger.warn("NullPointerException in takeScreenshotAndSend, screenshot not sent");
            logger.trace(npe);
        }
    }

    private void takeScreenshotAndSend(String action, WebDriver driver) {
        Command command = new Command(action, "n/a", "n/a");
        takeScreenshotAndSend(command, driver);
    }

    private void takeScreenshotAndSend(String action, WebDriver driver, Throwable thrw) {

        UITestLifecycle.get().getCapture().takeAndSendScreenshotWithError(
                new Command(action, "n/a", "n/a"),
                driver,
                thrw.getMessage() + "\n" + ExceptionUtils.getStackTrace(thrw));
    }

    private void sendFinalScreenshot(ITestResult result, String action) {
        if (ScreenshotCapture.isRequired() && isUITest(result.getInstance().getClass())) {
            Throwable thrw = result.getThrowable();
            WebDriver driver = UITestLifecycle.get().getWebDriver();
            if (null != thrw) {
                takeScreenshotAndSend(action, driver, thrw);
            } else {
                Command command = new Command(action, "n/a", "n/a");
                takeScreenshotAndSend(command, driver);
            }
        }
    }

    private boolean isUITest(Class<?> clazz) {
        return clazz.isAssignableFrom(BaseUITest.class)
                && UITestLifecycle.get().isInitialised();
    }

    private void highlightElementOnClickAndSendScreenshot(
            WebDriver driver, WebElement element) {
        if (!ScreenshotCapture.isRequired()) {
            return;
        }
        ElementHighlighter highlighter = new ElementHighlighter(driver);
        highlighter.highlightElement(element);
        Command command = new Command("click", element);
        takeScreenshotAndSend(command, driver);
        highlighter.unhighlightPrevious();
    }

    /* WebDriver events */
    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
        highlightElementOnClickAndSendScreenshot(driver, element);
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
    public void afterSwitchToWindow(String windowName, WebDriver driver) {
        Command command = new Command("nav", "window", windowName);
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
        return FRAMEWORKIUM_SCRIPTS.contains(script);
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
    public <X> void beforeGetScreenshotAs(OutputType<X> outputType) {}

    @Override
    public <X> void afterGetScreenshotAs(OutputType<X> outputType, X x) {}

    @Override
    public void beforeGetText(WebElement webElement, WebDriver webDriver) {}

    @Override
    public void afterGetText(WebElement webElement, WebDriver webDriver, String s) {}

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
    public void beforeSwitchToWindow(String windowName, WebDriver driver) {}

    @Override
    public void beforeFindBy(By by, WebElement element, WebDriver arg2) {}

    @Override
    public void beforeAlertAccept(WebDriver webDriver) {}

    @Override
    public void afterAlertAccept(WebDriver webDriver) {}

    @Override
    public void beforeAlertDismiss(WebDriver webDriver) {}

    @Override
    public void afterAlertDismiss(WebDriver webDriver) {}

    @Override
    public void onTestStart(ITestResult result) {}

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}

    @Override
    public void onStart(ITestContext context) {}

    @Override
    public void onFinish(ITestContext context) {}
}
