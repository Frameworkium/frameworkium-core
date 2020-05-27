package com.frameworkium.integration.capture.api.service;

import com.frameworkium.lite.api.services.BaseService;
import com.frameworkium.integration.capture.api.constant.CaptureEndpoint;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

/** Base Service for Capture specific services. */
public class BaseCaptureService extends BaseService {

    /**
     * @return a Rest Assured {@link RequestSpecification} with the baseUri
     *         (and anything else required by most Capture services).
     */
    @Override
    protected RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .baseUri(CaptureEndpoint.BASE_URI.getUrl())
                .relaxedHTTPSValidation() // trusts even invalid certs
                // .log().all() // uncomment to log each request
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    /**
     * @return a Rest Assured {@link ResponseSpecification} with basic checks
     *         (and anything else required by most Capture services).
     */
    @Override
    protected ResponseSpecification getResponseSpec() {
        return RestAssured.expect().response()
                .statusCode(HttpStatus.SC_OK);
    }

    protected ValidatableResponse post(String url, Object body) {
        return getRequestSpec()
                .when()
                .body(body)
                .post(url)
                .then()
                .assertThat().statusCode(HttpStatus.SC_CREATED);
    }

}
