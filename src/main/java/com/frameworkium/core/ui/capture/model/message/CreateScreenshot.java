package com.frameworkium.core.ui.capture.model.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.frameworkium.core.ui.capture.model.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateScreenshot {

    public Command command;
    public String url;
    public String executionID;
    public String errorMessage;
    public String screenshotBase64;

    // transient to prevent logger getting serialised to JSON
    private transient Logger logger = LogManager.getLogger(CreateScreenshot.class);

    public CreateScreenshot(
            String executionID, Command command, String url,
            String errorMessage, String screenshotBase64) {

        logger.debug(
                String.format("Creating screenshot: executionID='%s', " +
                                "Command.action='%s', url='%s', " +
                                "errorMessage='%s', screenshotBase64.length=%d",
                        executionID,
                        command.action, url,
                        errorMessage, screenshotBase64.length()));
        this.executionID = executionID;
        this.command = command;
        this.url = url;
        this.errorMessage = errorMessage;
        this.screenshotBase64 = screenshotBase64;
    }
}
