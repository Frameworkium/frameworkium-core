package com.frameworkium.core.ui.capture;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.capture.model.Command;
import com.frameworkium.core.ui.capture.model.message.CreateExecution;
import com.frameworkium.core.ui.capture.model.message.CreateScreenshot;
import com.frameworkium.core.ui.driver.Driver;
import com.frameworkium.core.ui.driver.DriverSetup;
import com.frameworkium.core.ui.driver.remotes.BrowserStack;
import com.frameworkium.core.ui.driver.remotes.Sauce;
import com.frameworkium.core.ui.tests.BaseTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;

import java.net.*;

import static com.frameworkium.core.common.properties.Property.CAPTURE_URL;
import static org.apache.http.HttpStatus.SC_CREATED;

public class ScreenshotCapture {

    private static final Logger logger = LogManager.getLogger();

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
        } catch (Throwable t) {
            logger.error("Unable to create Capture execution.", t);
            return null;
        }
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
                } catch (Throwable t) {
                    logger.warn("Failed to get node address of remote web driver");
                    logger.debug(t);
                }
            }
        } else {
            try {
                node = InetAddress.getLocalHost().getCanonicalHostName();
            } catch (UnknownHostException e) {
                logger.warn("Failed to get local machine name");
                logger.debug(e);
            }
        }
        return node;
    }

    private RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON);
    }

    private String getRemoteNodeAddress() throws MalformedURLException {
        return RestAssured
                .post(getTestSessionURL())
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
                BaseTest.getDriver().getWrappedRemoteWebDriver().getSessionId());
    }

    public static boolean isRequired() {
        return Property.allCapturePropertiesSpecified() && !Driver.isNative();
    }

    public void takeAndSendScreenshot(Command command, WebDriver driver) {
        takeAndSendScreenshotWithError(command, driver, null);
    }

    public void takeAndSendScreenshotWithError(
            Command command, WebDriver driver, String errorMessage) {

        CreateScreenshot createScreenshotMessage =
                new CreateScreenshot(
                        executionID,
                        command,
                        driver.getCurrentUrl(),
                        errorMessage,
                        getBase64Screenshot(driver));
        sendScreenshot(createScreenshotMessage);
    }

    private String getBase64Screenshot(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }

    private void sendScreenshot(CreateScreenshot createScreenshotMessage) {

        if (executionID == null) {
            logger.debug("No Screenshot sent. Capture didn't initialise for " + testID);
            return;
        }

        BaseTest.screenshotExecutor.execute(() -> {
            logger.debug("About to send screenshot to Capture for " + testID);
            try {
                getRequestSpec()
                        .body(createScreenshotMessage)
                        .when()
                        .post(CAPTURE_URL.getValue() + "/screenshot")
                        .then()
                        .assertThat().statusCode(SC_CREATED);
                logger.debug("Sent screenshot to Capture for " + testID);
            } catch (Throwable t) {
                logger.warn("Failed sending screenshot to Capture for " + testID);
                logger.debug(t);
            }
        });
    }
}
