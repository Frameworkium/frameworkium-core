package com.frameworkium.capture;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import com.frameworkium.config.DriverSetup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.frameworkium.capture.model.Command;
import com.frameworkium.capture.model.message.CreateExecution;
import com.frameworkium.capture.model.message.CreateScreenshot;
import com.frameworkium.config.DriverType;
import com.frameworkium.config.SystemProperty;
import com.frameworkium.config.WebDriverWrapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

public class ScreenshotCapture {

    private static final Logger logger = LogManager.getLogger(ScreenshotCapture.class);

    private String executionID;

    //Uncomment if needing to use with a proxy
    //static {
    //    RestAssured.proxy("1.2.3.4",8080);
    //}

    public ScreenshotCapture(String testID, WebDriver webdriver) {
        initExecution(new CreateExecution(testID, getNode(webdriver)));
    }

    public static boolean isRequired() {
        return SystemProperty.CAPTURE_URL.isSpecified() && !DriverType.isNative();
    }

    public void takeAndSendScreenshot(
            Command command, WebDriver webdriver, String errorMessage) {

        String url = DriverType.isNative() ? "" : webdriver.getCurrentUrl();
        TakesScreenshot ts = ((TakesScreenshot) webdriver);
        String screenshotBase64 = ts.getScreenshotAs(OutputType.BASE64);
        CreateScreenshot message =
                new CreateScreenshot(executionID, command, url, errorMessage, screenshotBase64);
        sendScreenshot(message);
    }

    private void sendScreenshot(CreateScreenshot createScreenshotMessage) {

        String uri = SystemProperty.CAPTURE_URL.getValue() + "/screenshot";

        RestAssured.given().contentType(ContentType.JSON)
                .body(createScreenshotMessage)
                .post(uri);
    }

    private void initExecution(CreateExecution createExecutionMessage) {

        String uri = SystemProperty.CAPTURE_URL.getValue() + "/executions";

        executionID =
                RestAssured.given().contentType(ContentType.JSON)
                        .body(createExecutionMessage).post(uri)
                        .then().statusCode(201)
                        .extract().path("executionID").toString();
        logger.debug("executionID=" + executionID);
    }

    private String getNode(WebDriver webdriver) {

        String node = "n/a";
        if (DriverSetup.useRemoteDriver()) {
            try {
                RemoteWebDriver r = ((WebDriverWrapper) webdriver).getWrappedRemoteWebDriver();
                URL gridURL = new URL(SystemProperty.GRID_URL.getValue());
                String url =
                        String.format("%s://%s:%d/grid/api/testsession?session=%s",
                                gridURL.getProtocol(), gridURL.getHost(),
                                gridURL.getPort(), r.getSessionId());
                node = RestAssured.post(url).then().extract().path("proxyId");
            } catch (Throwable t) {
                logger.warn("Failed to get node address of remote web driver", t);
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
