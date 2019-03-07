package com.frameworkium.core.ui.litelements.loader.decorator;

import com.frameworkium.core.ui.litelements.element.LitElement;
import com.frameworkium.core.ui.litelements.loader.decorator.proxyhandlers.LitElementListNamedProxyHandler;
import com.frameworkium.core.ui.litelements.loader.decorator.proxyhandlers.LitElementNamedProxyHandler;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.pagefactory.CustomElementLocatorFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.util.List;

import static com.frameworkium.core.ui.litelements.loader.LitElementLoader.createHtmlElement;
import static com.frameworkium.core.ui.litelements.loader.LitElementLoader.createLitElement;
import static com.frameworkium.core.ui.litelements.loader.decorator.ProxyFactoryLitElement.createLitElementListProxy;
import static com.frameworkium.core.ui.litelements.utils.LitElementUtils.isLitElement;
import static com.frameworkium.core.ui.litelements.utils.LitElementUtils.isLitElementList;
import static ru.yandex.qatools.htmlelements.loader.decorator.ProxyFactory.createWebElementProxy;
import static ru.yandex.qatools.htmlelements.utils.HtmlElementUtils.*;


public class LitElementDecorator extends HtmlElementDecorator {

    public LitElementDecorator(final CustomElementLocatorFactory factory) {
        super(factory);
    }

    @Override
    public Object decorate(ClassLoader loader, Field field) {
        try {
            if (isLitElement(field)) {
                return decorateLitElement(loader, field);
            }
            if (isTypifiedElement(field)) {
                return decorateTypifiedElement(loader, field);
            }
            if (isHtmlElement(field)) {
                return decorateHtmlElement(loader, field);
            }
            if (isWebElement(field) && !field.getName().equals("wrappedElement")) {
                return decorateWebElement(loader, field);
            }
            if (isLitElementList(field)) {
                return decorateLitElementList(loader, field);
            }
            if (isTypifiedElementList(field)) {
                return decorateTypifiedElementList(loader, field);
            }
            if (isHtmlElementList(field)) {
                return decorateHtmlElementList(loader, field);
            }
            if (isWebElementList(field)) {
                return decorateWebElementList(loader, field);
            }
            return null;
        } catch (ClassCastException ignore) {
            return null; // See bug #94 and NonElementFieldsTest
        }
    }

    protected <T extends LitElement> T decorateLitElement(ClassLoader loader, Field field) {
        WebElement elementToWrap = decorateShadowedWebElement(loader, field);
        return createLitElement((Class<T>) field.getType(), elementToWrap, getElementName(field));
    }

    protected WebElement decorateShadowedWebElement(ClassLoader loader, Field field) {
        ElementLocator locator = factory.createLocator(field);
        InvocationHandler handler = new LitElementNamedProxyHandler(locator, getElementName(field));

        return createWebElementProxy(loader, handler);
    }

    protected <T extends LitElement> List<T> decorateLitElementList(ClassLoader loader, Field field) {
        @SuppressWarnings("unchecked")
        Class<T> elementClass = (Class<T>) getGenericParameterClass(field);
        ElementLocator locator = factory.createLocator(field);
        String name = getElementName(field);

        InvocationHandler handler = new LitElementListNamedProxyHandler<>(elementClass, locator, name);

        return createLitElementListProxy(loader, handler);
    }

    @Override
    protected <T extends HtmlElement> T decorateHtmlElement(ClassLoader loader, Field field) {
        WebElement elementToWrap = decorateWebElement(loader, field);

        //noinspection unchecked
        return createHtmlElement((Class<T>) field.getType(), elementToWrap, getElementName(field));
    }
}
