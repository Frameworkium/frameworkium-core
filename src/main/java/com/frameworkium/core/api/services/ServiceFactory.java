package com.frameworkium.core.api.services;

import com.jayway.restassured.http.Method;
import com.jayway.restassured.specification.RequestSpecification;

public class ServiceFactory {

    public static <T extends BaseService<T>> T newInstance(
            Class<T> clazz, String url) {
        return newServiceInstance(clazz).get(url);
    }

    public static <T extends BaseService<T>> T newInstance(
            Class<T> clazz, String url, RequestSpecification requestSpec) {
        return newServiceInstance(clazz).get(url, requestSpec);
    }

    public static <T extends BaseService<T>> T newInstance(
            Class<T> clazz, String url, Method httpVerb, RequestSpecification requestSpec) {
        return newServiceInstance(clazz).get(url, httpVerb, requestSpec);
    }

    private static <T extends BaseService<T>> T newServiceInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Unable to instantiate ServiceObject", e);
        }
    }
}
