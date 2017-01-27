package com.frameworkium.core.ui.listeners;

import com.frameworkium.core.ui.capture.VideoCapture;
import org.openqa.selenium.remote.SessionId;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import static com.frameworkium.core.ui.capture.VideoCapture.isRequired;
import static com.frameworkium.core.ui.tests.BaseTest.getDriver;
import static com.frameworkium.core.ui.tests.BaseTest.getFrameworkDriver;

public class VideoListener extends TestListenerAdapter {

    private VideoCapture videoCapture = new VideoCapture();

    @Override public void onTestFailure(ITestResult iTestResult) {
        if (isRequired()) {
            SessionId sessionId = getDriver().getWrappedRemoteWebDriver().getSessionId();
            if (sessionId == null) return;
            getFrameworkDriver().tearDown();
            videoCapture.saveVideo(iTestResult, sessionId);
        }
    }
}
