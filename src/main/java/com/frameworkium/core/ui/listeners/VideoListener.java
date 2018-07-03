package com.frameworkium.core.ui.listeners;

import com.frameworkium.core.ui.capture.VideoCapture;
import com.frameworkium.core.ui.tests.BaseUITest;
import org.testng.*;

import static com.frameworkium.core.ui.capture.VideoCapture.isRequired;

public class VideoListener extends TestListenerAdapter {

    @Override
    public void onTestStart(ITestResult iTestResult) {
        if (isRequired()) {
            VideoCapture.addTest(iTestResult, BaseUITest.getThreadSessionId());
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
