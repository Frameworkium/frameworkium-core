package com.frameworkium.core.api.services;

import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.http.Method;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;

import static com.jayway.restassured.RestAssured.config;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.Method.GET;

public abstract class BaseService<T> {

    protected final static Logger logger = LogManager.getLogger();

    private Response response;

    protected RequestSpecification requestSpecification = given();
    protected Method method = GET;
    protected RestAssuredConfig restAssuredConfig = config;

    private Class<T> typeClass;
    private Class<T> typeArrayClass;

    @SuppressWarnings("unchecked")
    public BaseService() {
        typeClass = (Class<T>) ((ParameterizedType)getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
        typeArrayClass = (Class<T>) Array.newInstance(typeClass, 0).getClass();
    }

    @SuppressWarnings("unchecked")
    protected T newInstance(String url) {
        try {
            setResponse(url);
            return response.as(typeClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected T[] newArrayInstance(String url) {
        try {
            setResponse(url);
            return (T[]) response.as(typeArrayClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Response getResponse() {
        return response;
    }

    private void setResponse(String url) {
        requestSpecification.config(restAssuredConfig);
        switch (method) {
            case GET:
                response = requestSpecification.get(url);
                break;
            case PUT:
                response = requestSpecification.put(url);
                break;
            case POST:
                response = requestSpecification.post(url);
                break;
            case DELETE:
                response = requestSpecification.delete();
                break;
            case HEAD:
                response = requestSpecification.head();
                break;
            case PATCH:
                response = requestSpecification.patch();
                break;
            case TRACE:
                throw new IllegalStateException("TRACE not supported");
            case OPTIONS:
                response = requestSpecification.options();
                break;
        }
    }

}
