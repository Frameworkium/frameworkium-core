package com.frameworkium.capture.model.message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.frameworkium.capture.model.Command;

public class CreateScreenshot {

    private Command command;
    private String url;
    private String executionID;
    private String errorMessage;
    private String screenshotBase64;

    // transient to prevent logger getting serialised to JSON!
    private transient Logger logger = LogManager.getLogger(CreateScreenshot.class);

    public CreateScreenshot(
            String executionID, Command command, String url,
            String errorMessage, String screenshotBase64) {

        logger.debug(
                String.format("Creating screenshot: executionID='%s', " +
                                "Command.action='%s', url='%s', " +
                                "errorMessage='%s', screenshotBase64.length=%d",
                        executionID,
                        command.getAction(), url,
                        errorMessage, screenshotBase64.length()));
        this.executionID = executionID;
        this.command = command;
        this.url = url;
        this.errorMessage = errorMessage;
        this.screenshotBase64 = screenshotBase64;
    }

    public Command getCommand() {
        return command;
    }

    public String getUrl() {
        return url;
    }

    public String getExecutionID() {
        return executionID;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getScreenshotBase64() {
        return screenshotBase64;
    }
}
