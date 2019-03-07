package com.frameworkium.core.ui.litelements.loader.decorator.proxyhandlers;

import com.frameworkium.core.ui.litelements.element.LitElement;
import com.frameworkium.core.ui.litelements.utils.ShadowDomUtil;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.*;
import java.util.LinkedList;
import java.util.List;

import static com.frameworkium.core.ui.litelements.loader.LitElementLoader.createLitElement;


public class LitElementListNamedProxyHandler<T extends LitElement> implements InvocationHandler {
    private final Class<T> elementClass;
    private final ElementLocator locator;
    private final String name;

    public LitElementListNamedProxyHandler(Class<T> elementClass, ElementLocator locator, String name) {
        this.elementClass = elementClass;
        this.locator = locator;
        this.name = name;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if ("toString".equals(method.getName())) {
            return name;
        }

        List<T> elements = new LinkedList<>();
        int elementNumber = 0;
        for (WebElement element : locator.findElements()) {
            String newName = String.format("%s [%d]", name, elementNumber++);
            elements.add(createLitElement(elementClass, ShadowDomUtil.getShadow(element), newName));
        }

        try {
            return method.invoke(elements, objects);
        } catch (InvocationTargetException e) {
            // Unwrap the underlying exception
            throw e.getCause();
        }
    }
}