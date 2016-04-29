package com.frameworkium.core.ui.listeners;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.driver.DriverSetup.SupportedBrowsers;
import com.frameworkium.core.ui.driver.WebDriverWrapper;
import com.frameworkium.core.ui.tests.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import ru.yandex.qatools.allure.annotations.Attachment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.frameworkium.core.common.properties.Property.BROWSER;
import static com.frameworkium.core.ui.driver.DriverSetup.SupportedBrowsers.ELECTRON;

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
        if (!Property.CAPTURE_URL.isSpecified()) {
            try {
                String screenshotDirectory = System.getProperty("screenshotDirectory");
                if (null == screenshotDirectory) {
                    screenshotDirectory = "screenshots";
                }
                String absolutePath =
                        screenshotDirectory + File.separator + System.currentTimeMillis() + "_" + testName + ".png";
                File screenshot = new File(absolutePath);
                if (createFile(screenshot)) {
                    WebDriverWrapper driver = BaseTest.getDriver();
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

    /**
     * Are screenshots supported by the browser type being used
     *
     * @return boolean - true/false to whether screenshots are supported
     */
    private boolean isScreenshotSupported() {
        if (BROWSER.isSpecified()) {
            return !SupportedBrowsers.valueOf(BROWSER.getValue().toUpperCase()).equals(ELECTRON);
        } else {
            return false;
        }
    }

    @Override
    public void onTestFailure(ITestResult failingTest) {
        if (isScreenshotSupported()) takeScreenshot(failingTest.getName());
    }

    @Override
    public void onTestSkipped(ITestResult skippedTest) {
        if (isScreenshotSupported()) takeScreenshot(skippedTest.getName());
    }
}
