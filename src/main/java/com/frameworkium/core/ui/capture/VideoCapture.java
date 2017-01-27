package com.frameworkium.core.ui.capture;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.remote.SessionId;
import org.testng.ITestResult;
import ru.yandex.qatools.allure.annotations.Attachment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.frameworkium.core.common.properties.Property.VIDEO_CAPTURE_URL;
import static com.frameworkium.core.ui.tests.BaseTest.getDriver;

public class VideoCapture {

    private static final String videoFolder = "capturedVideo";
    private static final SimpleDateFormat videoNameDateFormat = new SimpleDateFormat("dd-mm-yyyy-HH-mm-ss");
    private static final Logger logger = LogManager.getLogger();

    private URL videoCaptureUrl;

    public static boolean isRequired() {
        return VIDEO_CAPTURE_URL.isSpecified();
    }

    public void saveVideo(ITestResult iTestResult, SessionId sessionId) {
        if (getDriver() == null) return;

        try {
            setVideoCaptureUrl(sessionId);
        }
        catch (MalformedURLException e) {
            logger.error("Unable to parse Grid URL", e);
            return;
        }

        String currentDateTime = videoNameDateFormat.format(new Date());
        String testName = iTestResult.getName();

        byte[] rawVideo;
        try {
            rawVideo = getVideo();
        } catch (InterruptedException | TimeoutException e) {
            logger.error(String.format("Timed out waiting for Session ID %s to become available after 6 seconds.", sessionId));
            return;
        }

        Path path = Paths.get(videoFolder);
        try {
            if (!Files.exists(path)) Files.createDirectory(path);
            String fileName = String.format(
                    "%s/%s-%s.%s",
                    videoFolder,
                    testName,
                    currentDateTime,
                    videoCaptureUrl.getFile().substring(videoCaptureUrl.getFile().lastIndexOf("."))
            );
            Files.write(Paths.get(fileName), rawVideo);
            logger.info(String.format("Captured video from grid: %s", fileName));
        }
        catch (IOException e) {
            logger.error("Failed creating directory/file for video capture", e);
        }
    }

    private void setVideoCaptureUrl(SessionId sessionId) throws MalformedURLException {
        this.videoCaptureUrl = new URL(String.format(VIDEO_CAPTURE_URL.getValue(), sessionId));
    }

    @Attachment(value = "Video on failure")
    private byte[] getVideo() throws TimeoutException, InterruptedException {
        int i = 0;
        while (i++ < 4) {
            Response response = RestAssured.get(videoCaptureUrl);
            if (response.getStatusCode() == HttpStatus.SC_OK) return response.asByteArray();
            TimeUnit.SECONDS.sleep(2);
        }
        throw new TimeoutException();
    }
}
