package com.frameworkium.core.ui.video;

import static com.frameworkium.core.common.properties.Property.VIDEO_CAPTURE_URL;

import io.qameta.allure.Attachment;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VideoCapture {

  private static final Path VIDEO_FOLDER = Paths.get("capturedVideo");

  private static final Logger logger = LogManager.getLogger();

  private static Map<String, String> testSessionMap = new ConcurrentHashMap<>();

  private String videoCaptureUrl;
  private UrlFetcher urlFetcher;

  public VideoCapture() {
    videoCaptureUrl = VIDEO_CAPTURE_URL.getValue();
    urlFetcher = new UrlFetcher();
  }

  public VideoCapture(String videoCaptureUrl, UrlFetcher urlFetcher) {
    this.videoCaptureUrl = videoCaptureUrl;
    this.urlFetcher = urlFetcher;
  }

  public static boolean isRequired() {
    return VIDEO_CAPTURE_URL.isSpecified();
  }

  public static void saveTestSessionID(String testName, String sessionId) {
    testSessionMap.put(testName, sessionId);
  }

  /**
   * Fetch video from remote server and save to video folder.
   */
  public void fetchAndSaveVideo(String testName) {
    String sessionId = testSessionMap.get(testName);
    URL videoCaptureURL = getVideoCaptureURL(sessionId);
    byte[] rawVideo = getVideo(videoCaptureURL);

    if (rawVideo != null) {
      String fileName = getFileName(testName, sessionId, videoCaptureURL.getFile());
      saveVideo(fileName, rawVideo);
    }
  }

  private URL getVideoCaptureURL(String sessionId) {
    try {
      return new URL(String.format(videoCaptureUrl, sessionId));
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Video Capture URL provided was invalid", e);
    }
  }

  @Attachment(value = "Video on Failure", type = "video/mp4")
  private byte[] getVideo(URL videoCaptureURL) {
    try {
      return urlFetcher.fetchWithRetry(videoCaptureURL, 4);
    } catch (TimeoutException e) {
      logger.error("Failed fetching URL {}.", videoCaptureURL);
      return null;
    }
  }

  private String getFileName(String testName, String sessionId, String fileFromUrl) {
    String fileExt = fileFromUrl.substring(fileFromUrl.lastIndexOf('.') + 1);
    return String.format("%s-%s.%s", testName, sessionId, fileExt);
  }

  private void saveVideo(String fileName, byte[] rawVideo) {
    try {
      Files.createDirectories(VIDEO_FOLDER);
      Files.write(VIDEO_FOLDER.resolve(fileName), rawVideo);
      logger.info("Captured video: {}", fileName);
    } catch (IOException e) {
      logger.error("Failed to save " + fileName, e);
    }
  }
}
