package com.frameworkium.core.ui.listeners;

import com.frameworkium.core.ui.tests.BaseUITest;
import com.frameworkium.core.ui.video.VideoCapture;
import org.testng.*;

public class VideoListener extends TestListenerAdapter {

    @Override
    public void onTestStart(ITestResult iTestResult) {
        if (VideoCapture.isRequired()) {
            VideoCapture.saveTestSessionID(
                    iTestResult.getName(),
                    BaseUITest.getThreadSessionId());
        }
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        if (VideoCapture.isRequired()) {
            VideoCapture videoCapture = new VideoCapture();
            iTestContext
                    .getFailedTests()
                    .getAllResults()
                    .stream()
                    .map(ITestResult::getName)
                    .forEach(videoCapture::fetchAndSaveVideo);
        }
    }

}
