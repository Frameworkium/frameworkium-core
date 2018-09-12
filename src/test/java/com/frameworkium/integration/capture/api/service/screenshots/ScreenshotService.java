package com.frameworkium.integration.capture.api.service.screenshots;

import com.frameworkium.integration.capture.api.constant.CaptureEndpoint;
import com.frameworkium.integration.capture.api.dto.screenshots.CreateScreenshot;
import com.frameworkium.integration.capture.api.service.BaseCaptureService;

/** Encapsulates the Capture ExecutionResponse service */
public class ScreenshotService extends BaseCaptureService {

    public void createScreenshot(CreateScreenshot createMessage) {
        post(CaptureEndpoint.SCREENSHOT.getUrl(), createMessage);
    }

}
