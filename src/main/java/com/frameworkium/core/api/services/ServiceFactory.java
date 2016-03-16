package com.frameworkium.core.api.services;

import com.frameworkium.core.ui.driver.WebDriverGuiceModule;
import com.frameworkium.core.ui.pages.BasePage;
import com.google.inject.Guice;

public class ServiceFactory {

    public static <T extends BaseService<T>> T newInstance(Class<T> clazz) {
        return Guice.createInjector(new ServicesGuiceModule()).getInstance(clazz).get();
    }

    public static <T extends BaseService<T>> T newInstance(Class<T> clazz, String url) {
        return Guice.createInjector(new ServicesGuiceModule()).getInstance(clazz).get(url);
    }
}
