package com.frameworkium.core.ui.listeners;

import static com.frameworkium.core.common.properties.Property.BROWSER;
import static com.frameworkium.core.ui.driver.DriverSetup.Browser.ELECTRON;

import com.frameworkium.core.ui.UITestLifecycle;
import com.frameworkium.core.ui.capture.ScreenshotCapture;
import com.frameworkium.core.ui.driver.DriverSetup.Browser;
import com.frameworkium.core.ui.tests.BaseUITest;
import io.qameta.allure.Attachment;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

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

  @Attachment(value = "Screenshot on failure", type = "image/png")
  private byte[] writeScreenshotToFile(TakesScreenshot driver, Path screenshot) {
    try (OutputStream screenshotStream = Files.newOutputStream(screenshot)) {
      byte[] bytes = driver.getScreenshotAs(OutputType.BYTES);
      screenshotStream.write(bytes);
      screenshotStream.close();
      return bytes;
    } catch (IOException e) {
      logger.error("Unable to write " + screenshot, e);
    } catch (WebDriverException e) {
      logger.error("Unable to take screenshot.", e);
    }
    return null;
  }

  private boolean isScreenshotSupported(ITestResult testResult) {
    boolean isElectron = BROWSER.isSpecified()
        && ELECTRON.equals(Browser.valueOf(BROWSER.getValue().toUpperCase()));
    boolean isUITest = testResult.getInstance() instanceof BaseUITest;
    return isUITest && !isElectron;
  }
}
