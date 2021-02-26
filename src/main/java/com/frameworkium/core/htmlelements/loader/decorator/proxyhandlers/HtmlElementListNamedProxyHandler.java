package com.frameworkium.core.htmlelements.loader.decorator.proxyhandlers;

import com.frameworkium.core.htmlelements.element.HtmlElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.frameworkium.core.htmlelements.loader.HtmlElementLoader.createHtmlElement;

public class HtmlElementListNamedProxyHandler<T extends HtmlElement> implements InvocationHandler {

    private final Class<T> elementClass;
    private final ElementLocator locator;
    private final String name;

    public HtmlElementListNamedProxyHandler(Class<T> elementClass, ElementLocator locator, String name) {
        this.elementClass = elementClass;
        this.locator = locator;
        this.name = name;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if ("toString".equals(method.getName())) {
            return name;
        }

        List<T> elements = locator.findElements().stream()
                .map(element -> createHtmlElement(elementClass, element))
                .collect(Collectors.toCollection(LinkedList::new));

        try {
            return method.invoke(elements, objects);
        } catch (InvocationTargetException e) {
            // Unwrap the underlying exception
            throw e.getCause();
        }
    }
}
