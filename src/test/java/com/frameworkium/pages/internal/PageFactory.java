package com.frameworkium.pages.internal;

import com.frameworkium.config.WebDriverGuiceModule;
import com.google.inject.Guice;

public class PageFactory {

    public static <T extends BasePage<T>> T newInstance(Class<T> clazz) {
        return Guice.createInjector(new WebDriverGuiceModule()).getInstance(clazz)
                .get();
    }

    public static <T extends BasePage<T>> T newInstance(Class<T> clazz,
            String url) {
        return Guice.createInjector(new WebDriverGuiceModule()).getInstance(clazz)
                .get(url);
    }
}
