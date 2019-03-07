package com.frameworkium.core.ui.litelements.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import static com.frameworkium.core.ui.tests.BaseUITest.getWebDriver;

public class ShadowDomUtil {

    private ShadowDomUtil() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    public static WebElement getShadow(WebElement webElement) {
        return (WebElement) ((JavascriptExecutor) getWebDriver())
                .executeScript("return arguments[0].shadowRoot", webElement);
    }
}
