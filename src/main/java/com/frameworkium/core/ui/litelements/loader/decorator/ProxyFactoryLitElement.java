package com.frameworkium.core.ui.litelements.loader.decorator;

import com.frameworkium.core.ui.litelements.element.LitElement;
import ru.yandex.qatools.htmlelements.loader.decorator.ProxyFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

public class ProxyFactoryLitElement extends ProxyFactory {

    public static <T extends LitElement> List<T> createLitElementListProxy(ClassLoader loader,
                                                                           InvocationHandler handler) {
        return (List<T>) Proxy.newProxyInstance(loader, new Class[]{List.class}, handler);
    }
}
