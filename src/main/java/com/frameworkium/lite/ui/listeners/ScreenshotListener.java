package com.frameworkium.lite.ui.listeners;

import com.frameworkium.lite.ui.UITestLifecycle;
import com.frameworkium.lite.ui.capture.ScreenshotCapture;
import com.frameworkium.lite.ui.tests.BaseUITest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;

public class ScreenshotListener extends TestListenerAdapter {

    private static final Logger logger = LogManager.getLogger();
    private final boolean captureEnabled = ScreenshotCapture.isRequired();

    @Override
    public void onTestFailure(ITestResult failingTest) {
        if (!captureEnabled && isScreenshotSupported(failingTest)) {
            takeScreenshotAndSaveLocally(failingTest.getName());
        }
    }

    @Override
    public void onTestSkipped(ITestResult skippedTest) {
        if (!captureEnabled && isScreenshotSupported(skippedTest)) {
            takeScreenshotAndSaveLocally(skippedTest.getName());
        }
    }

    private void takeScreenshotAndSaveLocally(String testName) {
        takeScreenshotAndSaveLocally(
                testName, (TakesScreenshot) UITestLifecycle.get().getWebDriver());
    }

    private void takeScreenshotAndSaveLocally(String testName, TakesScreenshot driver) {
        String screenshotDirectory = System.getProperty("screenshotDirectory");
        if (screenshotDirectory == null) {
            screenshotDirectory = "screenshots";
        }
        String fileName = String.format(
                "%s_%s.png",
                System.currentTimeMillis(),
                testName);
        Path screenshotPath = Paths.get(screenshotDirectory);
        Path absolutePath = screenshotPath.resolve(fileName);
        if (createScreenshotDirectory(screenshotPath)) {
            writeScreenshotToFile(driver, absolutePath);
            logger.info("Written screenshot to " + absolutePath);
        } else {
            logger.error("Unable to create " + screenshotPath);
        }
    }

    private boolean createScreenshotDirectory(Path screenshotDirectory) {
        try {
            Files.createDirectories(screenshotDirectory);
        } catch (IOException e) {
            logger.error("Error creating screenshot directory", e);
        }
        return Files.isDirectory(screenshotDirectory);
    }

    private void writeScreenshotToFile(TakesScreenshot driver, Path screenshot) {
        try (OutputStream screenshotStream = Files.newOutputStream(screenshot)) {
            byte[] bytes = driver.getScreenshotAs(OutputType.BYTES);
            screenshotStream.write(bytes);
        } catch (IOException e) {
            logger.error("Unable to write " + screenshot, e);
        } catch (WebDriverException e) {
            logger.error("Unable to take screenshot.", e);
        }
    }

    private boolean isScreenshotSupported(ITestResult testResult) {
        return testResult.getInstance() instanceof BaseUITest;
    }
}
