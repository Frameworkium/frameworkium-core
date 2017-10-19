package com.frameworkium.core.ui.listeners;

import com.frameworkium.core.ui.capture.ScreenshotCapture;
import com.frameworkium.core.ui.driver.DriverSetup.Browser;
import com.frameworkium.core.ui.driver.WebDriverWrapper;
import com.frameworkium.core.ui.tests.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.Augmenter;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import ru.yandex.qatools.allure.annotations.Attachment;

import java.io.*;
import java.nio.file.*;

import static com.frameworkium.core.common.properties.Property.BROWSER;
import static com.frameworkium.core.ui.driver.DriverSetup.Browser.ELECTRON;

public class ScreenshotListener extends TestListenerAdapter {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void onTestFailure(ITestResult failingTest) {
        if (isScreenshotSupported()) {
            takeScreenshotAndSaveLocally(failingTest.getName());
        }
    }

    @Override
    public void onTestSkipped(ITestResult skippedTest) {
        if (isScreenshotSupported()) {
            takeScreenshotAndSaveLocally(skippedTest.getName());
        }
    }

    private void takeScreenshotAndSaveLocally(String testName) {
        // Take a local screenshot if capture is not enabled
        if (!ScreenshotCapture.isRequired()) {
            try {
                String screenshotDirectory = System.getProperty("screenshotDirectory");
                if (screenshotDirectory == null) {
                    screenshotDirectory = "screenshots";
                }
                String absolutePath =
                        screenshotDirectory + File.separator
                                + System.currentTimeMillis() + "_" + testName + ".png";
                Path screenshot = Paths.get(absolutePath);
                if (createScreenshotDirectory(screenshot.getParent())) {
                    WebDriverWrapper driver = BaseTest.getDriver();
                    try {
                        writeScreenshotToFile(driver, screenshot);
                    } catch (ClassCastException cce) {
                        // We need to augment our driver object
                        writeScreenshotToFile(new Augmenter().augment(driver), screenshot);
                    }
                    logger.info("Written screenshot to " + absolutePath);
                } else {
                    logger.error("Unable to create " + absolutePath);
                }
            } catch (Exception e) {
                logger.error("Unable to take screenshot - " + e);
            }
        }
    }

    private boolean createScreenshotDirectory(Path screenshotDirectory) {
        if (!Files.isDirectory(screenshotDirectory)) {
            try {
                Files.createDirectories(screenshotDirectory);
            } catch (IOException e) {
                logger.error("Error creating screenshot directory", e);
            }
        }
        return Files.isDirectory(screenshotDirectory);
    }

    @Attachment(value = "Screenshot on failure", type = "image/png")
    private byte[] writeScreenshotToFile(WebDriver driver, Path screenshot) {
        try (OutputStream screenshotStream = Files.newOutputStream(screenshot)) {
            byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            screenshotStream.write(bytes);
            screenshotStream.close();
            return bytes;
        } catch (IOException e) {
            logger.error("Unable to write " + screenshot, e);
        }
        return null;
    }

    private boolean isScreenshotSupported() {
        boolean isElectron = BROWSER.isSpecified()
                && ELECTRON.equals(Browser.valueOf(BROWSER.getValue().toUpperCase()));

        return !isElectron;
    }
}
