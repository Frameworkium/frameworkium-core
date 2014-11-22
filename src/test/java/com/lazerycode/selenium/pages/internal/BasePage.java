package com.lazerycode.selenium.pages.internal;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.inject.Inject;

public abstract class BasePage<T extends BasePage<T>> {

    @Inject
    protected WebDriver driver;

    @Inject
    protected WebDriverWait wait;
    
    @SuppressWarnings("unchecked")
    public T then() {
        return (T) this;
    }

    public abstract T get();

    public T get(String url) {
        return get();
    }
}
