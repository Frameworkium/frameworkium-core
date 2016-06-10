package com.frameworkium.integration.tfl.api.service;

import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.integration.tfl.api.constant.Endpoint;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

/** Base Service for TFL specific services. */
public class BaseTFLService extends BaseService {

    /**
     * @return a Rest Assured {@link RequestSpecification} with the baseUri
     * and anything else required by most TFL services.
     */
    @Override
    protected RequestSpecification getDefaultRequestSpecification() {
        return RestAssured.given().baseUri(Endpoint.BASE_URI.getUrl());
    }

    /**
     * @return a Rest Assured {@link ResponseSpecification} with default checks
     * and anything else required by most TFL services.
     */
    @Override
    protected ResponseSpecification getDefaultResponseSpecification() {
        return getDefaultRequestSpecification().expect().response().statusCode(200);
    }

}
