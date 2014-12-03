package com.bootstrapium.config;

import static com.bootstrapium.tests.internal.BaseTest.getDriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.inject.AbstractModule;

/**
 * Guice module for WebDriver injection.
 */
public class WebDriverGuiceModule extends AbstractModule {
    @Override
    protected final void configure() {
        WebDriver driver = getDriver();
        bind(WebDriver.class).toInstance(driver);
        bind(WebDriverWait.class).toInstance(new WebDriverWait(driver, 10));
    }

}
