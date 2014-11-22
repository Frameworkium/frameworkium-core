package com.lazerycode.selenium.pages.internal;

import com.google.inject.Guice;
import com.lazerycode.selenium.config.WebDriverGuiceModule;

public class PageFactory {

    public static <T extends BasePage<T>> T getInstance(Class<T> clazz) {
        return Guice.createInjector(new WebDriverGuiceModule()).getInstance(clazz)
                .get();
    }

    public static <T extends BasePage<T>> T newInstance(Class<T> clazz,
            String url) {
        return Guice.createInjector(new WebDriverGuiceModule()).getInstance(clazz)
                .get(url);
    }
}
