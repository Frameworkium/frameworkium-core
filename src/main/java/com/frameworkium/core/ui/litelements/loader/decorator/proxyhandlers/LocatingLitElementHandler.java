package com.frameworkium.core.ui.litelements.loader.decorator.proxyhandlers;

import com.frameworkium.core.ui.litelements.utils.ShadowDomUtil;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.*;

public class LocatingLitElementHandler implements InvocationHandler {
    protected final ElementLocator locator;

    public LocatingLitElementHandler(ElementLocator locator) {
        this.locator = locator;
    }

    /**
     * Locates a litelement by shadowhost locator and returns the shadowroot
     *
     * @param object  is the proxy of the given shadow host
     * @param method  is the method to be invoked by the proxied object
     * @param objects is the arguments of {@code method}
     * @return the shadowroot of given shadowhost
     */
    public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
        WebElement element;
        WebElement shadowroot;

        try {
            element = locator.findElement();
        } catch (NoSuchElementException e) {
            if ("toString".equals(method.getName())) {
                return "Proxy element for: " + locator.toString();
            }
            throw e;
        }

        shadowroot = ShadowDomUtil.getShadow(element);
        if (shadowroot == null) {
            throw new NoSuchElementException("No shadowroot found for " + locator.toString());
        }


        if ("getWrappedElement".equals(method.getName())) {
            return element;
        }

        try {
            return method.invoke(shadowroot, objects);
        } catch (InvocationTargetException e) {
            // Unwrap the underlying exception
            throw e.getCause();
        }
    }
}
