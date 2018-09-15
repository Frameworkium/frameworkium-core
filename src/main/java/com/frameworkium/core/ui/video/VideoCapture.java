package com.frameworkium.core.ui.video;

import io.qameta.allure.Attachment;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.TimeoutException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.frameworkium.core.common.properties.Property.VIDEO_CAPTURE_URL;

public class VideoCapture {

    private static final Path VIDEO_FOLDER = Paths.get("capturedVideo");
    private static final Logger logger = LogManager.getLogger();

    private static Map<String, String> testSessionMap = new HashMap<>();

    private VideoCapture() {
        // hide default constructor for this util class
    }

    public static boolean isRequired() {
        return VIDEO_CAPTURE_URL.isSpecified();
    }

    public static void saveTestSessionID(String testName, String sessionId) {
        testSessionMap.put(testName, sessionId);
    }

    /** Fetch video from remote server and save to video folder. */
    public static void fetchAndSaveVideo(String testName) {
        String sessionId = testSessionMap.get(testName);
        URL videoCaptureURL = getVideoCaptureURL(sessionId);
        byte[] rawVideo = getVideo(videoCaptureURL);

        if (rawVideo != null) {
            String fileName = getFileName(testName, sessionId, videoCaptureURL.getFile());
            saveVideo(fileName, rawVideo);
        }
    }

    private static URL getVideoCaptureURL(String sessionId) {
        try {
            return new URL(String.format(VIDEO_CAPTURE_URL.getValue(), sessionId));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Video Capture URL provided was invalid", e);
        }
    }

    @Attachment(value = "Video on Failure", type = "video/mp4")
    private static byte[] getVideo(URL videoCaptureURL) {
        try {
            return fetchVideo(videoCaptureURL);
        } catch (InterruptedException | TimeoutException e) {
            logger.error("Failed fetching URL {}.", videoCaptureURL);
            return null;
        }
    }

    private static byte[] fetchVideo(URL videoCaptureURL)
            throws TimeoutException, InterruptedException {
        logger.debug("Download URL for Video Capture: " + videoCaptureURL);
        for (int i = 0; i < 4; i++) {
            Response response = RestAssured.get(videoCaptureURL);
            if (response.getStatusCode() == HttpStatus.SC_OK) {
                return response.asByteArray();
            }
            logger.debug("Retrying Video Capture download: " + videoCaptureURL);
            TimeUnit.SECONDS.sleep(2);
        }
        throw new TimeoutException();
    }

    private static String getFileName(String testName, String sessionId, String fileFromUrl) {
        String fileExt = fileFromUrl.substring(fileFromUrl.lastIndexOf('.') + 1);
        return String.format("%s-%s.%s", testName, sessionId, fileExt);
    }

    private static void saveVideo(String fileName, byte[] rawVideo) {
        try {
            Files.createDirectories(VIDEO_FOLDER);
            Files.write(VIDEO_FOLDER.resolve(fileName), rawVideo);
            logger.info("Captured video: {}", fileName);
        } catch (IOException e) {
            logger.error("Failed to save " + fileName, e);
        }
    }
}
