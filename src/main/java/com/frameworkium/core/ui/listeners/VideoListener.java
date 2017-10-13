package com.frameworkium.core.ui.listeners;

import static com.frameworkium.core.ui.capture.VideoCapture.isRequired;

import com.frameworkium.core.ui.capture.VideoCapture;
import com.frameworkium.core.ui.tests.BaseTest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class VideoListener extends TestListenerAdapter {

    @Override
    public void onTestStart(ITestResult iTestResult) {
        if (isRequired()) {
            VideoCapture.addTest(iTestResult, BaseTest.getThreadSessionId());
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
