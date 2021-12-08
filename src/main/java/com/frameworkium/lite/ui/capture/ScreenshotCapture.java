package com.frameworkium.lite.ui.capture;

import com.frameworkium.lite.common.properties.Property;
import com.frameworkium.lite.ui.UITestLifecycle;
import com.frameworkium.lite.ui.capture.model.Command;
import com.frameworkium.lite.ui.capture.model.message.CreateExecution;
import com.frameworkium.lite.ui.capture.model.message.CreateScreenshot;
import com.frameworkium.lite.ui.driver.DriverSetup;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;

import java.net.*;
import java.util.concurrent.*;

import static com.frameworkium.lite.common.properties.Property.*;
import static org.apache.http.HttpStatus.SC_CREATED;

/** Takes and sends screenshots to "Capture" asynchronously. */
public class ScreenshotCapture {

    private static final Logger logger = LogManager.getLogger();

    /** Shared Executor for async sending of screenshot messages to capture. */
    private static final ExecutorService executorService =
            Executors.newFixedThreadPool(4);

    private final String testID;
    private final String executionID;

    public ScreenshotCapture(String testID) {
        logger.debug("About to initialise Capture execution for {}", testID);
        this.testID = testID;
        this.executionID = createExecution(new CreateExecution(testID, getNode()));
        logger.debug("Capture executionID={}", executionID);
    }

    private String createExecution(CreateExecution createExecution) {
        try {
            return getRequestSpec()
                    .body(createExecution)
                    .when()
                    .post(CaptureEndpoint.EXECUTIONS.getUrl())
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
            node = getRemoteNode(node);
        } else {
            try {
                node = InetAddress.getLocalHost().getCanonicalHostName();
            } catch (UnknownHostException e) {
                logger.debug("Failed to get local machine name", e);
            }
        }
        return node;
    }

    private String getRemoteNode(String defaultValue) {
        try {
            return getRemoteNodeAddress();
        } catch (Exception e) {
            logger.debug("Failed to get node address of remote web driver", e);
        }
        return defaultValue;
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
                UITestLifecycle.get().getRemoteSessionId());
    }

    public static boolean isRequired() {
        return CAPTURE_URL.isSpecified()
                && SUT_NAME.isSpecified()
                && SUT_VERSION.isSpecified();
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
            logger.error("Can't send Screenshot. "
                    + "Capture didn't initialise execution for test: {}", testID);
            return;
        }
        
        if(errorMessage.contains("Skipped after failure")){
            logger.warn("Not sending screenshot due to test being skipped.");
            return;
        }

        CreateScreenshot createScreenshotMessage =
                new CreateScreenshot(
                        executionID,
                        command,
                        driver.getCurrentUrl(),
                        errorMessage,
                        getBase64Screenshot((TakesScreenshot) driver));
        addScreenshotToSendQueue(createScreenshotMessage);
    }

    private String getBase64Screenshot(TakesScreenshot driver) {
        return driver.getScreenshotAs(OutputType.BASE64);
    }

    private void addScreenshotToSendQueue(CreateScreenshot createScreenshotMessage) {
        executorService.execute(() -> sendScreenshot(createScreenshotMessage));
    }

    private void sendScreenshot(CreateScreenshot createScreenshotMessage) {
        logger.trace("About to send screenshot to Capture for {}", testID);
        try {
            getRequestSpec()
                    .body(createScreenshotMessage)
                    .when()
                    .post(CaptureEndpoint.SCREENSHOT.getUrl())
                    .then()
                    .assertThat().statusCode(SC_CREATED);
            logger.trace("Sent screenshot to Capture for {}", testID);
        } catch (Exception e) {
            logger.warn("Failed sending screenshot to Capture for {}", testID);
            logger.debug(e);
        }
    }

    /** Waits up to 2 minutes to send any remaining Screenshot messages. */
    public static void processRemainingBacklog() {

        executorService.shutdown();

        if (!isRequired()) {
            return;
        }

        logger.info("Processing remaining Screenshot Capture backlog...");
        boolean timeout;
        try {
            timeout = !executorService.awaitTermination(2, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
        if (timeout) {
            logger.error("Shutdown timed out. "
                    + "Some screenshots might not have been sent.");
        } else {
            logger.info("Finished processing backlog.");
        }
    }
}
