package com.frameworkium.integration.restfulbooker.api.service;

import com.frameworkium.lite.api.services.BaseService;
import com.frameworkium.integration.restfulbooker.api.constant.BookerEndpoint;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

/** Base Service for RestfulBooker specific services. */
public abstract class AbstractBookerService extends BaseService {

    /**
     * @return a Rest Assured {@link RequestSpecification} with the baseUri
     *         (and anything else required by most Capture services).
     */
    @Override
    protected RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .baseUri(BookerEndpoint.BASE_URI.getUrl())
                .relaxedHTTPSValidation() // trusts even invalid certs
                // .log().all() // uncomment to log each request
                .contentType("application/json")
                .accept("application/json");
    }

    /**
     * @return a Rest Assured {@link ResponseSpecification} with basic checks
     *         (and anything else required by most services).
     */
    @Override
    protected ResponseSpecification getResponseSpec() {
        return RestAssured.expect().response()
                .statusCode(HttpStatus.SC_OK);
    }

    protected ExtractableResponse post(Object body, String url) {
        return getRequestSpec()
                .body(body)
                .when()
                .post(url)
                .then()
                .spec(getResponseSpec())
                .extract();
    }

}
