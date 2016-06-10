package com.frameworkium.core.api.services;

import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.jayway.restassured.RestAssured.config;

public abstract class BaseService {

    protected final static Logger logger = LogManager.getLogger();

    /**
     * Used to define the RequestSpecification common to all operations
     * defined in the given service. For example:
     * <pre><code>RestAssured.given().</code></pre>
     */
    protected abstract RequestSpecification getRequestSpecification();

    /**
     * Used to define the RequestSpecification common to all operations
     * defined in the given service. For example:
     * <pre><code>RestAssured.given().</code></pre>
     */
    protected abstract ResponseSpecification getResponseSpecification();

}
