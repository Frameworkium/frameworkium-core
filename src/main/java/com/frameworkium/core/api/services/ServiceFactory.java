package com.frameworkium.core.api.services;

import com.google.inject.Guice;
import com.jayway.restassured.http.Method;
import com.jayway.restassured.specification.RequestSpecification;

public class ServiceFactory {

    public static <T extends BaseService<T>> T newInstance(Class<T> clazz, String url) {
        return Guice.createInjector(new ServicesGuiceModule()).getInstance(clazz).get(url);
    }

    public static <T extends BaseService<T>> T newInstance(Class<T> clazz, String url, RequestSpecification requestSpec) {
        return Guice.createInjector(new ServicesGuiceModule()).getInstance(clazz).get(url,requestSpec);
    }
    public static <T extends BaseService<T>> T newInstance(Class<T> clazz, String url, Method httpVerb, RequestSpecification requestSpec) {
        return Guice.createInjector(new ServicesGuiceModule()).getInstance(clazz).get(url,httpVerb,requestSpec);
    }

}
