package com.frameworkium.integration.ai.capture.api.service.screenshots;

import com.frameworkium.integration.ai.capture.api.constant.CaptureEndpoint;
import com.frameworkium.integration.ai.capture.api.dto.screenshots.CreateScreenshot;
import com.frameworkium.integration.ai.capture.api.service.BaseCaptureService;

/** Encapsulates the Capture ExecutionResponse service */
public class ScreenshotService extends BaseCaptureService {

    public void createScreenshot(CreateScreenshot createMessage) {
        post(CaptureEndpoint.SCREENSHOT.getUrl(), createMessage);
    }

}
