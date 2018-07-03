package com.frameworkium.core.ui.capture;

import io.qameta.allure.Attachment;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.TimeoutException;
import org.testng.ITestResult;

import java.io.IOException;
import java.net.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.frameworkium.core.common.properties.Property.VIDEO_CAPTURE_URL;

public class VideoCapture {

    private static final String VIDEO_FOLDER = "capturedVideo";
    private static final Logger logger = LogManager.getLogger();

    private static HashMap<String, String> testMap = new HashMap<>();

    private VideoCapture() {
        // hide default constructor for this util class
    }

    public static boolean isRequired() {
        return VIDEO_CAPTURE_URL.isSpecified();
    }

    public static void addTest(ITestResult iTestResult, String sessionId) {
        testMap.put(iTestResult.getName(), sessionId);
    }

    /**
     * Save video to video folder.
     */
    public static void saveVideo(ITestResult iTestResult) {
        String sessionId = testMap.get(iTestResult.getName());
        URL videoCaptureURL = getVideoCaptureURL(sessionId);
        byte[] rawVideo;
        try {
            rawVideo = getVideo(videoCaptureURL);
        } catch (InterruptedException | TimeoutException e) {
            logger.error(String.format(
                    "Timed out waiting for Session ID %s to become available after 6 seconds.",
                    sessionId));
            return;
        } catch (ConnectException e) {
            logger.error(String.format(
                    "Connection was refused for Session ID %s while trying to retrieve the video.",
                    sessionId));
            return;
        }

        Path path = Paths.get(VIDEO_FOLDER);
        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            String fileName = String.format(
                    "%s/%s-%s.%s",
                    VIDEO_FOLDER,
                    iTestResult.getName(),
                    iTestResult.getEndMillis(),
                    videoCaptureURL.getFile().substring(videoCaptureURL.getFile().lastIndexOf(".") + 1)
            );
            Files.write(Paths.get(fileName), rawVideo);
            logger.info(String.format("Captured video from grid: %s", fileName));
        } catch (IOException e) {
            logger.error("Failed creating directory/file for video capture", e);
        }
    }

    private static URL getVideoCaptureURL(String sessionId) {
        try {
            return new URL(String.format(VIDEO_CAPTURE_URL.getValue(), sessionId));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Video Capture URL provided was invalid", e);
        }
    }

    @Attachment(value = "Video on Failure", type = "video/mp4")
    private static byte[] getVideo(URL videoCaptureURL)
            throws TimeoutException, InterruptedException, ConnectException {
        int i = 0;
        while (i++ < 4) {
            logger.debug("Download URL for Video Capture: " + videoCaptureURL);
            Response response = RestAssured.get(videoCaptureURL);
            if (response.getStatusCode() == HttpStatus.SC_OK) {
                return response.asByteArray();
            }
            TimeUnit.SECONDS.sleep(2);
        }
        throw new TimeoutException();
    }
}
