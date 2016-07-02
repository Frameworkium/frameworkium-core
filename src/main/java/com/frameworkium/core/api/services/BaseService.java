package com.frameworkium.core.api.services;

import com.google.common.collect.ImmutableMap;
import io.restassured.http.Method;
import io.restassured.response.ExtractableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public abstract class BaseService {

    protected final Logger logger = LogManager.getLogger();

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
     * @param url the url to get
     * @return The response from the request
     */
    protected ExtractableResponse request(String url) {
        return request(ImmutableMap.of(), url);
    }

    /**
     * Performs GET request of the URL with parameters.
     *
     * @param params the GET parameters
     * @param url    the URL to GET
     * @return The response from the request
     */
    protected ExtractableResponse request(Map<String, ?> params, String url) {
        return request(Method.GET, params, url);
    }

    /**
     * Performs specified HTTP verb request of the URL with parameters.
     *
     * @param method the HTTP method to request
     * @param params the request parameters
     * @param url    the URL to request
     * @return The response from the request
     */
    protected ExtractableResponse request(Method method, Map<String, ?> params, String url) {
        return getRequestSpec()
                .params(params)
                .when()
                .request(method, url)
                .then()
                .spec(getResponseSpec())
                .extract();
    }

}
