package com.frameworkium.capture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.frameworkium.capture.model.Command;
import com.frameworkium.capture.model.message.CreateExecution;
import com.frameworkium.capture.model.message.CreateScreenshot;
import com.frameworkium.config.SystemProperty;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

public class ScreenshotCapture {

    private static final Logger logger = LogManager.getLogger(ScreenshotCapture.class);

    private String executionID;

    public ScreenshotCapture(String testID) {
        initExecution(new CreateExecution(testID, "TODO"));
    }

    public void takeAndSendScreenshot(Command command, WebDriver webdriver, String errorMessage) {

        String url = webdriver.getCurrentUrl();
        // String node = webdriver.getNodeAddress();
        String screenshotBase64 = ((TakesScreenshot) webdriver).getScreenshotAs(OutputType.BASE64);
        CreateScreenshot message = new CreateScreenshot(executionID, command, url, errorMessage, screenshotBase64);
        sendScreenshot(message);
    }

    private void sendScreenshot(CreateScreenshot createScreenshotmessage) {

        String uri = SystemProperty.CAPTURE_URL.getValue() + "/screenshot";
        RestAssured.given().log().body().contentType(ContentType.JSON).body(createScreenshotmessage).when().post(uri)
                .then().log().body().statusCode(201);
    }

    private void initExecution(CreateExecution createExecutionMessage) {

        String uri = SystemProperty.CAPTURE_URL.getValue() + "/execution";
        executionID =
                RestAssured.given().log().body().contentType(ContentType.JSON).body(createExecutionMessage).when()
                        .post(uri).then().log().body().statusCode(201).extract().path("executionID").toString();
        logger.info("executionID = " + executionID);
    }

}
