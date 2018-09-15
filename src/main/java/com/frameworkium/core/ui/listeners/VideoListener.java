package com.frameworkium.core.ui.listeners;

import com.frameworkium.core.ui.video.VideoCapture;
import com.frameworkium.core.ui.tests.BaseUITest;
import org.testng.*;

import static com.frameworkium.core.ui.video.VideoCapture.isRequired;

public class VideoListener extends TestListenerAdapter {

    @Override
    public void onTestStart(ITestResult iTestResult) {
        if (isRequired()) {
            VideoCapture.saveTestSessionID(iTestResult, BaseUITest.getThreadSessionId());
        }
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        if (isRequired()) {
            iTestContext.getFailedTests()
                    .getAllResults()
                    .forEach(VideoCapture::saveVideo);
        }
    }

}
