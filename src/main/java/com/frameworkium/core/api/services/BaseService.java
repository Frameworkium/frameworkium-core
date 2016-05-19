package com.frameworkium.core.api.services;

import com.frameworkium.core.api.annotations.DeserialiseAs;
import com.frameworkium.core.common.reporting.allure.AllureLogger;
import com.jayway.restassured.http.Method;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

import static com.jayway.restassured.RestAssured.given;

public abstract class BaseService<T extends BaseService<T>> {

    protected final Logger logger = LogManager.getLogger(this);

    private Response response;

    @SuppressWarnings("unchecked")
    public T get(String url) {
        return get(url, Method.GET, given());
    }

    @SuppressWarnings("unchecked")
    public T get(String url, RequestSpecification requestSpec) {
        return get(url, Method.GET, requestSpec);
    }

    @SuppressWarnings("unchecked")
    public T get(String url, Method httpVerb, RequestSpecification requestSpec) {

        //make the request
        this.response = makeRequest(httpVerb, url, requestSpec);
        setFieldsBasedUponAnnotations();

        try {
            AllureLogger.logToAllure("Service '" + this.getClass().getName() + "' successfully loaded");
        } catch (Exception e) {
            logger.error("Error logging page load, but loaded successfully");
        }
        return (T) this;
    }

    private Response makeRequest(Method httpVerb, String url, RequestSpecification requestSpec) {

        //TODO - enable automatic logging to the logger - eg errors to error, rest to debug?
        //requestSpec = requestSpec.config(
        // RestAssured.config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails(HEADERS)));

        Response resp;

        logger.info("Making request: {} {}", httpVerb.toString(), url);
//        requestSpec = requestSpec.log().all();

        switch (httpVerb) {
            case GET:
                resp = requestSpec.get(url);
                break;
            case PUT:
                resp = requestSpec.put(url);
                break;
            case POST:
                resp = requestSpec.post(url);
                break;
            case DELETE:
                resp = requestSpec.delete(url);
                break;
            case HEAD:
                resp = requestSpec.head(url);
                break;
            case TRACE:
                throw new IllegalStateException("TRACE not supported");
            case OPTIONS:
                resp = requestSpec.options(url);
                break;
            case PATCH:
                resp = requestSpec.patch(url);
                break;
            default:
                logger.error("Unrecognised http verb supplied - defaulting to GET");
                resp = requestSpec.get(url);
        }

        return resp;
    }

    private void setFieldsBasedUponAnnotations() {
        for (Field field : this.getClass().getDeclaredFields()) {

            Object value = null;

            DeserialiseAs[] deserialiseAsAnnotations = field.getAnnotationsByType(DeserialiseAs.class);

            if (deserialiseAsAnnotations.length == 1) {
                Class fieldClass = field.getType();
                try {
                    value = response.as(fieldClass);
                } catch (Exception e) {
                    logger.error("Error de-serialising the response", e);
                    logger.error("Response received was:");
                    logger.error(response.body().asString());
                    throw new RuntimeException(e);
                }
            } else if (deserialiseAsAnnotations.length > 1) {
                throw new IllegalArgumentException(
                        "Cannot have more than 1 @DeserialiseAs per field");
            }

            if (value != null) {
                try {
                    field.setAccessible(true);
                    field.set(this, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public String getPrettyPrintedResponse() {
        return this.response.prettyPrint();
    }

    public int getResponseCode() {
        return response.getStatusCode();
    }

}
