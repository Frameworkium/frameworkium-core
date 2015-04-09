package com.frameworkium.capture;

import java.net.InetAddress;
import java.net.URL;

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
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

public class ScreenshotCapture {

    private static final Logger logger = LogManager.getLogger(ScreenshotCapture.class);

    private String executionID;

    public ScreenshotCapture(String testID, WebDriver webdriver) {
        initExecution(new CreateExecution(testID, getNode()));
    }
    
    public void takeAndSendScreenshot(Command command, WebDriver webdriver, String errorMessage) {

        String url = DriverType.isNative() ? "" : webdriver.getCurrentUrl();
        TakesScreenshot ts = ((TakesScreenshot) webdriver);
        String screenshotBase64 = ts.getScreenshotAs(OutputType.BASE64);
        CreateScreenshot message = new CreateScreenshot(executionID, command, url, errorMessage, screenshotBase64);
        sendScreenshot(message);
    }

    private void sendScreenshot(CreateScreenshot createScreenshotmessage) {

        String uri = SystemProperty.CAPTURE_URL.getValue() + "/screenshot";
        RestAssured.given().contentType(ContentType.JSON).body(createScreenshotmessage).when().post(uri)
                .then().log().headers().statusCode(201);
    }

    public static boolean isRequired() {
        return SystemProperty.CAPTURE_URL.isSpecified() && !DriverType.isNative();
    }

    private void initExecution(CreateExecution createExecutionMessage) {

        String uri = SystemProperty.CAPTURE_URL.getValue() + "/execution";
        executionID =
                RestAssured.given().log().body().contentType(ContentType.JSON).body(createExecutionMessage).when()
                        .post(uri).then().statusCode(201).extract().path("executionID").toString();
        logger.info("executionID = " + executionID);
    }

    private String getNode() {

        String node = "n/a";
        if (DriverType.useRemoteWebDriver) {
            try {
                RemoteWebDriver r = (RemoteWebDriver) webdriver;
                URL gridURL = new URL(SystemProperty.GRID_URL);
                String url = gridURL.getProtocol() + gridURL.getHost() + ':' + gridURL.getPort() 
                                + "/grid/api/testsession?session=" + r.getSessionId();
                node = RestAssured.post(url).andReturn().jsonPath().getString("proxyId");
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
