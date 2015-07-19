package com.frameworkium.listeners;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import ru.yandex.qatools.allure.annotations.Attachment;

import com.frameworkium.config.SystemProperty;
import com.frameworkium.tests.internal.BaseTest;

public class ScreenshotListener extends TestListenerAdapter {

    private static Logger logger = LogManager.getLogger(ScreenshotListener.class);

    private boolean createFile(File screenshot) {
        boolean fileCreated = false;

        if (screenshot.exists()) {
            fileCreated = true;
        } else {
            File parentDirectory = new File(screenshot.getParent());
            if (parentDirectory.exists() || parentDirectory.mkdirs()) {
                try {
                    fileCreated = screenshot.createNewFile();
                } catch (IOException e) {
                    logger.error("Error creating screenshot", e);
                }
            }
        }

        return fileCreated;
    }

    @Attachment(value = "Screenshot on failure", type = "image/png")
    private byte[] writeScreenshotToFile(WebDriver driver, File screenshot) {
        try {
            FileOutputStream screenshotStream = new FileOutputStream(screenshot);
            byte[] bytes = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.BYTES);
            screenshotStream.write(bytes);
            screenshotStream.close();
            return bytes;
        } catch (IOException e) {
            logger.error("Unable to write " + screenshot.getAbsolutePath(), e);
        }
        return null;
    }

    private void takeScreenshot(String testName) {
        // Take a local screenshot if capture is not enabled
        if (!SystemProperty.CAPTURE_URL.isSpecified()) {
            try {
                String screenshotDirectory = System.getProperty("screenshotDirectory");
                if (null == screenshotDirectory) {
                    screenshotDirectory = "screenshots";
                }
                String absolutePath =
                        screenshotDirectory + File.separator + System.currentTimeMillis() + "_" + testName + ".png";
                File screenshot = new File(absolutePath);
                if (createFile(screenshot)) {
                    WebDriver driver = BaseTest.getDriver();
                    try {
                        writeScreenshotToFile(driver, screenshot);
                    } catch (ClassCastException weNeedToAugmentOurDriverObject) {
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

    @Override
    public void onTestFailure(ITestResult failingTest) {
        takeScreenshot(failingTest.getName());
    }

    @Override
    public void onTestSkipped(ITestResult skippedTest) {
        takeScreenshot(skippedTest.getName());
    }
}
