package com.frameworkium.lite.htmlelements.loader.decorator.proxyhandlers;

import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.internal.LocatingElementListHandler;

import java.lang.reflect.Method;

public class WebElementListNamedProxyHandler extends LocatingElementListHandler {

    private final String name;

    public WebElementListNamedProxyHandler(ElementLocator locator, String name) {
        super(locator);
        this.name = name;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if ("toString".equals(method.getName())) {
            return name;
        }
        return super.invoke(o, method, objects);
    }
}
