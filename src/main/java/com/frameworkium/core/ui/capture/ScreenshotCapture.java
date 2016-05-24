package com.frameworkium.core.ui.capture;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.capture.model.Command;
import com.frameworkium.core.ui.capture.model.message.CreateExecution;
import com.frameworkium.core.ui.capture.model.message.CreateScreenshot;
import com.frameworkium.core.ui.driver.DriverSetup;
import com.frameworkium.core.ui.driver.DriverType;
import com.frameworkium.core.ui.driver.WebDriverWrapper;
import com.frameworkium.core.ui.driver.remotes.BrowserStack;
import com.frameworkium.core.ui.driver.remotes.Sauce;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import static com.frameworkium.core.common.properties.Property.CAPTURE_URL;
import static com.frameworkium.core.common.properties.Property.SUT_NAME;
import static com.frameworkium.core.common.properties.Property.SUT_VERSION;
import static com.frameworkium.core.ui.tests.BaseTest.executor;

public class ScreenshotCapture {

    private static final Logger logger = LogManager.getLogger(ScreenshotCapture.class);

    private String executionID;

    private ScreenshotCapture() {
    }

    public ScreenshotCapture(String testID, WebDriverWrapper driver) {
        initExecution(testID, driver);
    }

    public static boolean isRequired() {
        boolean allRequirePropertiesSpecified =
                CAPTURE_URL.isSpecified() &&
                SUT_NAME.isSpecified() &&
                SUT_VERSION.isSpecified();

        return allRequirePropertiesSpecified && !DriverType.isNative();
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
            logger.debug("Capture executionID is null.");
            return;
        }

        String uri = CAPTURE_URL.getValue() + "/screenshot";

        executor.submit(() -> {
            try {
                logger.debug("About to send screenshot to Capture.");
                RestAssured.given().contentType(ContentType.JSON)
                        .body(createScreenshotMessage)
                        .post(uri)
                        .then()
                        .assertThat().statusCode(201);
            } catch (Throwable t) {
                logger.warn("Failed sending screenshot to Capture.");
                logger.debug(t);
            }
        });
    }

    private void initExecution(String testID, WebDriverWrapper driver) {

        String uri = CAPTURE_URL.getValue() + "/executions";

        try {
            logger.debug("About to initialise Capture execution.");
            CreateExecution createExecution =
                    new CreateExecution(testID, getNode(driver));
            executionID = RestAssured
                    .given().contentType(ContentType.JSON)
                    .body(createExecution)
                    .post(uri)
                    .then().statusCode(201)
                    .extract().path("executionID").toString();
            logger.debug("Capture executionID=" + executionID);
        } catch (Throwable t) {
            logger.warn("Unable to create Capture execution.", t);
        }
    }

    private String getNode(WebDriverWrapper driver) {

        // TODO: can this be cached?
        String node = "n/a";
        if (DriverSetup.useRemoteDriver()) {
            if (Sauce.isDesired()) {
                node = "SauceLabs";
            } else if (BrowserStack.isDesired()) {
                node = "BrowserStack";
            } else {
                try {
                    RemoteWebDriver remoteDriver = driver.getWrappedRemoteWebDriver();
                    URL gridURL = new URL(Property.GRID_URL.getValue());
                    String url =
                            String.format("%s://%s:%d/grid/api/testsession?session=%s",
                                    gridURL.getProtocol(), gridURL.getHost(),
                                    gridURL.getPort(), remoteDriver.getSessionId());
                    node = RestAssured.post(url).then().extract().path("proxyId");
                } catch (Throwable t) {
                    logger.warn("Failed to get node address of remote web driver", t);
                }
            }
        } else {
            try {
                node = InetAddress.getLocalHost().getCanonicalHostName();
            } catch (UnknownHostException e) {
                logger.warn("Failed to get local machine name", e);
            }
        }
        return node;
    }
}
