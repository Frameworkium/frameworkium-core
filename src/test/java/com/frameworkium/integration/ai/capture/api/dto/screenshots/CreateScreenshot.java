package com.frameworkium.integration.ai.capture.api.dto.screenshots;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.frameworkium.core.api.dto.AbstractDTO;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateScreenshot extends AbstractDTO<CreateScreenshot> {

    public Command command;
    public String url;
    public String executionID;
    public String errorMessage;
    public String screenshotBase64;

    public static CreateScreenshot newInstance(String executionID) {
        CreateScreenshot createScreenshot = new CreateScreenshot();
        createScreenshot.executionID = executionID;
        createScreenshot.command = Command.newInstance();
        createScreenshot.url = "http://test.url/hello?x=1&y=2";
        createScreenshot.errorMessage = null;
        createScreenshot.screenshotBase64 = getBase64TestImage();
        return createScreenshot;
    }

    private static String getBase64TestImage() {
        try {
            InputStream imageStream = CreateScreenshot.class.getClassLoader()
                    .getResourceAsStream("capture-screenshot.png");
            byte[] bytes = IOUtils.toByteArray(imageStream);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
