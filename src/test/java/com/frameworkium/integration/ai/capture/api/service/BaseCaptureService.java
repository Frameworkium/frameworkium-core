package com.frameworkium.integration.ai.capture.api.service;

import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.integration.ai.capture.api.constant.CaptureEndpoint;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

/** Base Service for Capture specific services. */
public class BaseCaptureService extends BaseService {

    /**
     * @return a Rest Assured {@link RequestSpecification} with the baseUri
     * (and anything else required by most Capture services).
     */
    @Override
    protected RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .baseUri(CaptureEndpoint.BASE_URI.getUrl())
                .log().all() // uncomment to log each request
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    /**
     * @return a Rest Assured {@link ResponseSpecification} with basic checks
     * (and anything else required by most Capture services).
     */
    @Override
    protected ResponseSpecification getResponseSpec() {
        return RestAssured.expect().response()
                .statusCode(HttpStatus.SC_OK);
    }

}
