package com.frameworkium.lite.htmlelements.element;

import com.frameworkium.lite.common.properties.Property;
import com.frameworkium.lite.ui.UITestLifecycle;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static com.frameworkium.lite.htmlelements.utils.HtmlElementUtils.*;

/** Represents web page file upload element. */
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

        sendKeys(getFilePath(fileName));
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
            var webDriver = UITestLifecycle.get().getWebDriver();
            var efDriver = (EventFiringWebDriver) webDriver;
            var remoteDriver = (RemoteWebDriver) efDriver.getWrappedDriver();
            remoteDriver.setFileDetector(new LocalFileDetector());
        }
    }

    /**
     * Submits selected file by simply submitting the whole form, which contains this file input.
     */
    @Override
    public void submit() {
        getWrappedElement().submit();
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
        return new File(fileName).getPath();
    }
}
