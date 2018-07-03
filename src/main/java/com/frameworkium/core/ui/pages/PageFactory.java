package com.frameworkium.core.ui.pages;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;

public class PageFactory {

    private PageFactory() {}

    public static <T extends BasePage<T>> T newInstance(Class<T> clazz) {
        return instantiatePageObject(clazz).get();
    }

    @Deprecated
    public static <T extends BasePage<T>> T newInstance(
            Class<T> clazz, long timeoutInSeconds) {
        return instantiatePageObject(clazz).get(timeoutInSeconds);
    }

    public static <T extends BasePage<T>> T newInstance(
            Class<T> clazz, Duration timeout) {
        return instantiatePageObject(clazz).get(timeout);
    }

    public static <T extends BasePage<T>> T newInstance(
            Class<T> clazz, String url) {
        return instantiatePageObject(clazz).get(url);
    }

    @Deprecated
    public static <T extends BasePage<T>> T newInstance(
            Class<T> clazz, String url, long timeoutInSeconds) {
        return instantiatePageObject(clazz).get(url, timeoutInSeconds);
    }

    public static <T extends BasePage<T>> T newInstance(
            Class<T> clazz, String url, Duration timeout) {
        return instantiatePageObject(clazz).get(url, timeout);
    }

    private static <T extends BasePage<T>> T instantiatePageObject(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException
                | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException("Unable to instantiate PageObject", e);
        }
    }
}
