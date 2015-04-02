package com.frameworkium.capture.model.message;

import com.frameworkium.capture.model.Command;

public class CreateScreenshot {

    private Command command;
    private String url;
    private String executionID;
    private String errorMessage;
    private String screenshotBase64;

    public CreateScreenshot(String executionID, Command command, String url, String errorMessage,
            String screenshotBase64) {

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
