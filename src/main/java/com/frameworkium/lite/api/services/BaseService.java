package com.frameworkium.lite.api.services;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.ResponseOptions;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides structure to a "Service" object that represents actions to be
 * performed using an API.
 */
public abstract class BaseService {

    protected final Logger logger = LogManager.getLogger(this);

    /**
     * Used to define the RequestSpecification common to all operations
     * defined in the given service. For example:
     * <pre><code>RestAssured.given().proxy(...)</code></pre>
     *
     * @return the RestAssured RequestSpecification with appropriate defaults
     */
    protected abstract RequestSpecification getRequestSpec();

    /**
     * Used to define the RequestSpecification common to all operations
     * defined in the given service. For example:
     * <pre>
     *     <code>getDefaultRequestSpecification().then().response().statusCode(200);</code>
     * </pre>
     *
     * @return the RestAssured ResponseSpecification with appropriate defaults
     */
    protected abstract ResponseSpecification getResponseSpec();

    /**
     * Performs GET request of the URL.
     *
     * @return The response from the request
     */
    protected ExtractableResponse<? extends ResponseOptions<?>> get(String url) {
        return get(url, new HashMap<>());
    }

    /**
     * Performs GET request of the URL with parameters.
     *
     * @param url    the URL to GET
     * @param params the GET parameters
     * @return The response from the request
     */
    protected ExtractableResponse<? extends ResponseOptions<?>> get(String url, Map<String, ?> params) {
        return getRequestSpec()
                .params(params)
                .when()
                .get(url)
                .then()
                .spec(getResponseSpec())
                .extract();
    }

}
