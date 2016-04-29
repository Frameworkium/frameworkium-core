package com.frameworkium.core.ui.pages;

import com.frameworkium.core.ui.driver.WebDriverGuiceModule;
import com.google.inject.Guice;

public class PageFactory {

    public static <T extends BasePage<T>> T newInstance(Class<T> clazz) {
        return Guice
                .createInjector(new WebDriverGuiceModule())
                .getInstance(clazz)
                .get();
    }

    public static <T extends BasePage<T>> T newInstance(
            Class<T> clazz, Integer timeoutInSeconds) {
        return Guice
                .createInjector(new WebDriverGuiceModule())
                .getInstance(clazz)
                .get(timeoutInSeconds);
    }

    public static <T extends BasePage<T>> T newInstance(
            Class<T> clazz, String url) {
        return Guice
                .createInjector(new WebDriverGuiceModule())
                .getInstance(clazz)
                .get(url);
    }

    public static <T extends BasePage<T>> T newInstance(
            Class<T> clazz, String url, Integer timeoutInSeconds) {
        return Guice
                .createInjector(new WebDriverGuiceModule())
                .getInstance(clazz)
                .get(url, timeoutInSeconds);
    }
}
