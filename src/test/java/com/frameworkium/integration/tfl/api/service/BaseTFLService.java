package com.frameworkium.integration.tfl.api.service;

import com.frameworkium.core.api.services.BaseService;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class BaseTFLService extends BaseService {

    @Override
    protected RequestSpecification getRequestSpecification() {
        return RestAssured.given().log().all();
    }

    @Override
    protected ResponseSpecification getResponseSpecification() {
        return RestAssured.expect().response().statusCode(200);
    }
}
