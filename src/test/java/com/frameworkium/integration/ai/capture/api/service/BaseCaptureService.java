package com.frameworkium.integration.ai.capture.api.service;

import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.integration.ai.capture.api.constant.CaptureEndpoint;
import com.frameworkium.integration.ai.capture.api.dto.executions.BaseDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.ValidatableResponse;
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
                .relaxedHTTPSValidation() // trusts even invalid certs
                // .log().all() // uncomment to log each request
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

    protected ValidatableResponse get(String url) {
        return request(Method.GET, url);
    }

    protected ValidatableResponse post(String url, BaseDTO<?> body){
        return request(Method.POST, url, body);
    }

    private ValidatableResponse request(Method post, String url, BaseDTO<?> body) {
        return getRequestSpec()
                .when()
                .body(body)
                .request(post, url)
                .then();
    }

    private ValidatableResponse request(Method get, String url){
        return getRequestSpec()
                .when()
                .request(get, url)
                .then();
    }

}
