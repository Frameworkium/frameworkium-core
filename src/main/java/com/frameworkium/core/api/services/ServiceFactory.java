package com.frameworkium.core.api.services;

import com.google.inject.Guice;
import com.jayway.restassured.response.Response;

public class ServiceFactory {

    public static <T extends BaseService<T>> T newInstance(Class<T> clazz, Response response) {
        return Guice.createInjector(new ServicesGuiceModule()).getInstance(clazz).get(response);
    }
}
