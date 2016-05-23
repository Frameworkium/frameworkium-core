package com.frameworkium.core.ui.pages;

public class PageFactory {

    public static <T extends BasePage<T>> T newInstance(Class<T> clazz) {

        return getPageObject(clazz).get();
    }

    public static <T extends BasePage<T>> T newInstance(
            Class<T> clazz, long timeoutInSeconds) {

        return getPageObject(clazz).get(timeoutInSeconds);
    }

    public static <T extends BasePage<T>> T newInstance(
            Class<T> clazz, String url) {

        return getPageObject(clazz).get(url);
    }

    public static <T extends BasePage<T>> T newInstance(
            Class<T> clazz, String url, long timeoutInSeconds) {

        return getPageObject(clazz).get(url, timeoutInSeconds);
    }

    private static <T extends BasePage<T>> T getPageObject(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Unable to instantiate PageObject", e);
        }
    }
}
