package com.frameworkium.core.ui.driver;

import com.frameworkium.core.ui.tests.BaseTest;
import com.google.inject.AbstractModule;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

/** Guice module for WebDriver injection. */
public class WebDriverGuiceModule extends AbstractModule {

    @Override
    protected final void configure() {
        // Inject WebDriver
        WebDriver driver = BaseTest.getDriver();
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
        bind(WebDriver.class).toInstance(driver);

        // Inject Wait
        bind(WebDriverWait.class).toInstance(new WebDriverWait(driver, 10));
    }

}
