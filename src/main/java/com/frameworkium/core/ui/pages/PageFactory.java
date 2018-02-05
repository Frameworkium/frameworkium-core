package com.frameworkium.core.ui.pages;

public class PageFactory {

    private PageFactory() {}

    public static <T extends BasePage<T>> T newInstance(Class<T> clazz) {
        return instantiatePageObject(clazz).get();
    }

    public static <T extends BasePage<T>> T newInstance(
            Class<T> clazz, long timeoutInSeconds) {
        return instantiatePageObject(clazz).get(timeoutInSeconds);
    }

    public static <T extends BasePage<T>> T newInstance(
            Class<T> clazz, String url) {
        return instantiatePageObject(clazz).get(url);
    }

    public static <T extends BasePage<T>> T newInstance(
            Class<T> clazz, String url, long timeoutInSeconds) {
        return instantiatePageObject(clazz).get(url, timeoutInSeconds);
    }

    private static <T extends BasePage<T>> T instantiatePageObject(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Unable to instantiate PageObject", e);
        }
    }
}
