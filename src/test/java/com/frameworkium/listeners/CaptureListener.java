package com.frameworkium.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.frameworkium.capture.model.Command;
import com.frameworkium.config.WebDriverWrapper;
import com.frameworkium.tests.internal.BaseTest;

public class CaptureListener implements WebDriverEventListener, ITestListener {

    private static final Logger logger = LogManager.getLogger(CaptureListener.class);

    private void takeScreenshotAndSendToCapture(Command command, WebDriver driver) {
        BaseTest.getCapture().takeAndSendScreenshot(command, driver, null);
    }

    private void takeScreenshotAndSendToCapture(String action, WebDriver driver) {
        Command command = new Command(action, "n/a", "n/a");
        takeScreenshotAndSendToCapture(command, driver);
    }

    private void takeScreenshotAndSendToCaptureWithException(String action, WebDriver driver, Throwable thrw) {
        Command command = new Command(action, "n/a", "n/a");
        BaseTest.getCapture().takeAndSendScreenshot(command, driver,
                thrw.getMessage() + "\n" + thrw.getStackTrace().toString());
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

    /* WebDriver events */
    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
        Command command = new Command("click", element);
        takeScreenshotAndSendToCapture(command, driver);
    }

    @Override
    public void beforeNavigateBack(WebDriver driver) {
        takeScreenshotAndSendToCapture("navigate back", driver);
    }

    @Override
    public void beforeNavigateForward(WebDriver driver) {
        takeScreenshotAndSendToCapture("navigate forward", driver);
    }

    @Override
    public void beforeNavigateTo(String url, WebDriver driver) {
        Command command = new Command("navigate", "url", url);
        takeScreenshotAndSendToCapture(command, driver);
    }

    @Override
    public void beforeScript(String script, WebDriver driver) {
        takeScreenshotAndSendToCapture("run script", driver);
    }

    @Override
    public void onException(Throwable thrw, WebDriver driver) {
        // takeScreenshotAndSendToCaptureWithException("onException()", driver, thrw);
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

    /*
     * Methods we don't really want screenshots for.
     */

    @Override
    public void afterChangeValueOf(WebElement element, WebDriver driver) {}

    @Override
    public void afterClickOn(WebElement element, WebDriver driver) {}

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
    public void beforeChangeValueOf(WebElement element, WebDriver driver) {}

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
