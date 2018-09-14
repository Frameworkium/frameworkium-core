package com.frameworkium.core.ui.capture;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.capture.model.Command;
import com.frameworkium.core.ui.capture.model.message.CreateExecution;
import com.frameworkium.core.ui.capture.model.message.CreateScreenshot;
import com.frameworkium.core.ui.driver.Driver;
import com.frameworkium.core.ui.driver.DriverSetup;
import com.frameworkium.core.ui.driver.remotes.BrowserStack;
import com.frameworkium.core.ui.driver.remotes.Sauce;
import com.frameworkium.core.ui.tests.BaseUITest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;

import java.net.*;
import java.util.concurrent.*;

import static com.frameworkium.core.common.properties.Property.*;
import static org.apache.http.HttpStatus.SC_CREATED;

/**
 * Takes and sends screenshots to "Capture" asynchronously.
 */
public class ScreenshotCapture {

    private static final Logger logger = LogManager.getLogger();

    /** Shared Executor for async sending of screenshot messages to capture. */
    private static ExecutorService executorService;

    private String executionID;
    private String testID;

    public ScreenshotCapture(String testID) {
        this.testID = testID;
        logger.debug("About to initialise Capture execution for " + testID);
        this.executionID = createExecution(new CreateExecution(testID, getNode()));
        logger.debug("Capture executionID=" + executionID);
    }

    private String createExecution(CreateExecution createExecution) {

        try {
            return getRequestSpec()
                    .body(createExecution)
                    .when()
                    .post(CAPTURE_URL.getValue() + "/executions")
                    .then().statusCode(SC_CREATED)
                    .extract().path("executionID").toString();
        } catch (Exception e) {
            logger.error("Unable to create Capture execution.", e);
            return null;
        }
    }

    private RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON);
    }

    private String getNode() {

        String node = "n/a";
        if (DriverSetup.useRemoteDriver()) {
            if (Sauce.isDesired()) {
                node = "SauceLabs";
            } else if (BrowserStack.isDesired()) {
                node = "BrowserStack";
            } else {
                try {
                    node = getRemoteNodeAddress();
                } catch (Exception e) {
                    logger.warn("Failed to get node address of remote web driver");
                    logger.debug(e);
                }
            }
        } else {
            try {
                node = InetAddress.getLocalHost().getCanonicalHostName();
            } catch (UnknownHostException e) {
                logger.debug("Failed to get local machine name", e);
            }
        }
        return node;
    }

    private String getRemoteNodeAddress() throws MalformedURLException {
        return RestAssured
                .get(getTestSessionURL())
                .then()
                .extract().jsonPath()
                .getString("proxyId");
    }

    private String getTestSessionURL() throws MalformedURLException {
        URL gridURL = new URL(Property.GRID_URL.getValue());
        return String.format(
                "%s://%s:%d/grid/api/testsession?session=%s",
                gridURL.getProtocol(),
                gridURL.getHost(),
                gridURL.getPort(),
                BaseUITest.getThreadSessionId());
    }

    public static boolean isRequired() {
        boolean allCapturePropertiesSpecified = CAPTURE_URL.isSpecified()
                && SUT_NAME.isSpecified()
                && SUT_VERSION.isSpecified();
        return allCapturePropertiesSpecified && !Driver.isNative();
    }

    public void takeAndSendScreenshot(Command command, WebDriver driver) {
        takeAndSendScreenshotWithError(command, driver, null);
    }

    /**
     * Take and send a screenshot with an error message.
     */
    public void takeAndSendScreenshotWithError(
            Command command, WebDriver driver, String errorMessage) {

        if (executionID == null) {
            logger.error("Can't send Screenshot." +
                    " Capture didn't initialise execution for test: " + testID);
            return;
        }

        CreateScreenshot createScreenshotMessage =
                new CreateScreenshot(
                        executionID,
                        command,
                        driver.getCurrentUrl(),
                        errorMessage,
                        getBase64Screenshot((TakesScreenshot) driver));
        sendScreenshot(createScreenshotMessage);
    }

    private String getBase64Screenshot(TakesScreenshot driver) {
        return driver.getScreenshotAs(OutputType.BASE64);
    }

    private void sendScreenshot(CreateScreenshot createScreenshotMessage) {
        getScreenshotExecutor().execute(() -> {
            logger.debug("About to send screenshot to Capture for {}", testID);
            try {
                getRequestSpec()
                        .body(createScreenshotMessage)
                        .when()
                        .post(CAPTURE_URL.getValue() + "/screenshot")
                        .then()
                        .assertThat().statusCode(SC_CREATED);
                logger.debug("Sent screenshot to Capture for " + testID);
            } catch (Exception e) {
                logger.warn("Failed sending screenshot to Capture for " + testID);
                logger.debug(e);
            }
        });
    }

    private synchronized ExecutorService getScreenshotExecutor() {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(4);
        }
        return executorService;
    }

    /**
     * Waits to finish processing any remaining queued Screenshot messages.
     */
    public static void processRemainingBacklog() {
        if (!isRequired() || executorService == null) {
            return;
        }

        logger.info("Processing remaining Screenshot Capture backlog...");
        executorService.shutdown();

        boolean timeout;
        try {
            timeout = !executorService.awaitTermination(2, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (timeout) {
            logger.error("Shutdown timed out. "
                    + "Some screenshots might not have been sent.");
        } else {
            logger.info("Finished processing backlog.");
        }
    }
}
