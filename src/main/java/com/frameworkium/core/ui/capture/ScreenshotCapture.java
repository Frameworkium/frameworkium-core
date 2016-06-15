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
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.*;

import static com.frameworkium.core.common.properties.Property.*;
import static com.frameworkium.core.ui.tests.BaseTest.executor;

public class ScreenshotCapture {

    private static final Logger logger = LogManager.getLogger(ScreenshotCapture.class);

    private String executionID;

    private ScreenshotCapture() {
    }

    public ScreenshotCapture(String testID) {
        initExecution(testID);
    }

    public static boolean isRequired() {
        boolean allRequirePropertiesSpecified =
                CAPTURE_URL.isSpecified() &&
                        SUT_NAME.isSpecified() &&
                        SUT_VERSION.isSpecified();

        return allRequirePropertiesSpecified && !Driver.isNative();
    }

    private void initExecution(String testID) {

        String uri = CAPTURE_URL.getValue() + "/executions";

        try {
            logger.debug("About to initialise Capture execution.");
            CreateExecution createExecution =
                    new CreateExecution(testID, getNode());
            executionID = getRequestSpec()
                    .body(createExecution)
                    .when()
                    .post(uri)
                    .then().statusCode(201)
                    .extract().path("executionID").toString();
            logger.debug("Capture executionID=" + executionID);
        } catch (Throwable t) {
            logger.error("Unable to create Capture execution.", t);
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
                RemoteWebDriver remoteDriver = BaseTest.getDriver().getWrappedRemoteWebDriver();
                try {
                    URL gridURL = new URL(Property.GRID_URL.getValue());
                    String testSessionURI = String.format(
                            "%s://%s:%d/grid/api/testsession?session=%s",
                            gridURL.getProtocol(),
                            gridURL.getHost(),
                            gridURL.getPort(),
                            remoteDriver.getSessionId());

                    node = RestAssured
                            .post(testSessionURI)
                            .then()
                            .extract().jsonPath()
                            .getString("proxyId");
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

    public void takeAndSendScreenshot(
            Command command, WebDriver driver, String errorMessage) {

        sendScreenshot(
                new CreateScreenshot(
                        executionID,
                        command,
                        driver.getCurrentUrl(),
                        errorMessage,
                        getBase64Screenshot(driver))
        );
    }

    private String getBase64Screenshot(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }

    private void sendScreenshot(CreateScreenshot createScreenshotMessage) {

        if (executionID == null) {
            logger.debug("No Screenshot sent. Capture executionID is null.");
            return;
        }

        String uri = CAPTURE_URL.getValue() + "/screenshot";

        executor.execute(() -> {
            logger.debug("About to send screenshot to Capture.");
            try {
                getRequestSpec()
                        .body(createScreenshotMessage)
                        .when()
                        .post(uri)
                        .then()
                        .assertThat().statusCode(201);
                logger.debug("Sent screenshot to Capture.");
            } catch (Throwable t) {
                logger.warn("Failed sending screenshot to Capture.");
                logger.debug(t);
            }
        });
    }

    private RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .contentType(ContentType.JSON);
    }
}
