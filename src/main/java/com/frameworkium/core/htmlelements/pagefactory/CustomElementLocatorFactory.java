package com.frameworkium.core.htmlelements.pagefactory;

import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

/**
 * A factory for producing {@link ElementLocator}s. It is expected that a new
 * ElementLocator will be returned per call.
 */
public interface CustomElementLocatorFactory extends ElementLocatorFactory {
    ElementLocator createLocator(Class<?> clazz);
}
