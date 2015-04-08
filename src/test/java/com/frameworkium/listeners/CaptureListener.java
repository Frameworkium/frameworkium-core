package com.frameworkium.listeners;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.frameworkium.capture.ElementHighlighter;
import com.frameworkium.capture.ScreenshotCapture;
import com.frameworkium.capture.model.Command;
import com.frameworkium.config.DriverType;
import com.frameworkium.config.WebDriverWrapper;
import com.frameworkium.tests.internal.BaseTest;

public class CaptureListener implements WebDriverEventListener, ITestListener {

    private static final Logger logger = LogManager.getLogger(CaptureListener.class);

    private void takeScreenshotAndSendToCapture(Command command, WebDriver driver) {
        logger.debug("takeScreenshotAndSendToCapture");
        BaseTest.getCapture().takeAndSendScreenshot(command, driver, null);
    }

    private void takeScreenshotAndSendToCapture(String action, WebDriver driver) {
        Command command = new Command(action, "n/a", "n/a");
        takeScreenshotAndSendToCapture(command, driver);
    }

    private void takeScreenshotAndSendToCaptureWithException(String action, WebDriver driver, Throwable thrw) {
        Command command = new Command(action, "n/a", "n/a");
        BaseTest.getCapture().takeAndSendScreenshot(command, driver, thrw.getMessage() + "\n" + ExceptionUtils.getStackTrace(thrw));
    }

    private void sendFinalScreenshot(ITestResult result, String action) {
        WebDriverWrapper webDriver = BaseTest.getDriver();
        Throwable thrw = result.getThrowable();
        if (null != thrw) {
            takeScreenshotAndSendToCaptureWithException(action, webDriver, thrw);
        } else {
            Command command = new Command(action, "n/a", "n/a");
            takeScreenshotAndSendToCapture(command, webDriver);
        }
    }

    private void highlightElementTakeScreenshotAndSendToCapture(String action, WebDriver driver, WebElement element) {
        logger.debug(String.format("highlightElementTakeScreenshotAndSendToCapture: action=%s", action));
        ElementHighlighter highlighter = null;
        try {
            if (!DriverType.isNative()) {
                logger.debug("Trying to highlight the element");
                highlighter = new ElementHighlighter(driver);
                highlighter.highlightElement(element);
            }
            Command command = new Command(action, element);
            takeScreenshotAndSendToCapture(command, driver);
            if (null != highlighter) {
                highlighter.unhighlightLast();
            }
        } catch (StaleElementReferenceException serex) {
            logger.debug("Caught StaleElementReferenceException when trying to (un)highlight an element.");
        }
    }

    /* WebDriver events */
    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
        highlightElementTakeScreenshotAndSendToCapture("click", driver, element);
    }

    @Override
    public void afterChangeValueOf(WebElement element, WebDriver driver) {
        highlightElementTakeScreenshotAndSendToCapture("change", driver, element);
    }

    @Override
    public void beforeNavigateBack(WebDriver driver) {
        takeScreenshotAndSendToCapture("nav back", driver);
    }

    @Override
    public void beforeNavigateForward(WebDriver driver) {
        takeScreenshotAndSendToCapture("nav forward", driver);
    }

    @Override
    public void beforeNavigateTo(String url, WebDriver driver) {
        Command command = new Command("nav", "url", url);
        takeScreenshotAndSendToCapture(command, driver);
    }

    @Override
    public void beforeScript(String script, WebDriver driver) {
        takeScreenshotAndSendToCapture("script", driver);
    }

    /* Test end methods */

    @Override
    public void onTestSuccess(ITestResult result) {
        if (ScreenshotCapture.isRequired()) {
            sendFinalScreenshot(result, "pass");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (ScreenshotCapture.isRequired()) {
            sendFinalScreenshot(result, "fail");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (ScreenshotCapture.isRequired()) {
            sendFinalScreenshot(result, "skip");
        }
    }

    /*
     * Methods we don't really want screenshots for.
     */

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
