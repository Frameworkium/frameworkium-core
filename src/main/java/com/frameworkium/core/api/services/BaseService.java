package com.frameworkium.core.api.services;

import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public abstract class BaseService {

    protected final static Logger logger = LogManager.getLogger();

    /**
     * Used to define the RequestSpecification common to all operations
     * defined in the given service. For example:
     * <pre><code>RestAssured.given().proxy(...)</code></pre>
     */
    protected abstract RequestSpecification getDefaultRequestSpecification();

    /**
     * Used to define the RequestSpecification common to all operations
     * defined in the given service. For example:
     * <pre>
     *     <code>getDefaultRequestSpecification().expect().response().statusCode(200);</code>
     * </pre>
     */
    protected abstract ResponseSpecification getDefaultResponseSpecification();

    /**
     * Template method used to define the RequestSpecification with params.
     */
    protected ResponseSpecification getResponseSpecification(Map<String, ?> params) {
        return getRequestSpecification()
                .params(params)
                .response().spec(getResponseSpecification());
    }

    /**
     * Method to be overridden in Concrete Services if required.
     */
    protected ResponseSpecification getResponseSpecification() {
        return getDefaultResponseSpecification();
    }

    /**
     * Method to be overridden in Concrete Services if required.
     */
    protected RequestSpecification getRequestSpecification() {
        return getDefaultRequestSpecification();
    }

}
