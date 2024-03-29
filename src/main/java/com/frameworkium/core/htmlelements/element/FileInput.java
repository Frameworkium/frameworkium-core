package com.frameworkium.core.htmlelements.element;

import static com.frameworkium.core.htmlelements.utils.HtmlElementUtils.existsInClasspath;
import static com.frameworkium.core.htmlelements.utils.HtmlElementUtils.getResourceFromClasspath;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.UITestLifecycle;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.events.EventFiringWebDriver;

/**
 * Represents web page file upload element.
 */
public class FileInput extends TypifiedElement {

  /**
   * Specifies wrapped {@link WebElement}.
   *
   * @param wrappedElement {@code WebElement} to wrap.
   */
  public FileInput(WebElement wrappedElement) {
    super(wrappedElement);
  }

  /**
   * Sets a file to be uploaded.
   * <p>
   * File is searched in the following way: if a resource with a specified name exists in classpath,
   * then this resource will be used, otherwise file will be searched on file system.
   *
   * @param fileName Name of a file or a resource to be uploaded.
   */
  public void setFileToUpload(final String fileName) {
    // Set local file detector in case of remote driver usage
    setLocalFileDetectorIfRequired();

    String filePath = getFilePath(fileName);
    sendKeys(new File(filePath).toString());
  }

  /**
   * Sets multiple files to be uploaded.
   * <p>
   * Files are searched in the following way:
   * if a resource with a specified name exists in classpath,
   * then this resource will be used, otherwise file will be searched on file system.
   *
   * @param fileNames a list of file Names to be uploaded.
   */
  public void setFilesToUpload(List<String> fileNames) {
    // Set local file detector in case of remote driver usage
    setLocalFileDetectorIfRequired();

    String filePaths = fileNames.stream()
        .map(this::getFilePath)
        .collect(Collectors.joining("\n"));
    sendKeys(filePaths);
  }

  private void setLocalFileDetectorIfRequired() {
    if (Property.GRID_URL.isSpecified()) {
      WebDriver webDriver = UITestLifecycle.get().getWebDriver();
      EventFiringWebDriver efDriver = (EventFiringWebDriver) webDriver;
      RemoteWebDriver remoteDriver = (RemoteWebDriver) efDriver.getWrappedDriver();
      remoteDriver.setFileDetector(new LocalFileDetector());
    }
  }

  /**
   * Submits selected file by simply submitting the whole form, which contains this file input.
   */
  public void submit() {
    getWrappedElement().submit();
  }

  private WebElement getNotProxiedInputElement() {
    return getWrappedElement().findElement(By.xpath("."));
  }

  private void setLocalFileDetector(RemoteWebElement element) {
    element.setFileDetector(new LocalFileDetector());
  }

  private String getFilePath(final String fileName) {
    if (existsInClasspath(fileName)) {
      return getPathForResource(fileName);
    }
    return getPathForSystemFile(fileName);
  }

  private String getPathForResource(final String fileName) {
    return getResourceFromClasspath(fileName).getPath();
  }

  private String getPathForSystemFile(final String fileName) {
    File file = new File(fileName);
    return file.getPath();
  }
}
