package com.frameworkium.core.ui.litelements.loader.decorator.proxyhandlers;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.Method;
import java.time.LocalTime;

public class LitElementNamedProxyHandler extends LocatingLitElementHandler {

    public static final int DEFAULT_TIMEOUT = 5;

    private final long timeOutInSeconds;

    private final String name;

    public LitElementNamedProxyHandler(ElementLocator locator, String name) {
        super(locator);
        this.name = name;
        this.timeOutInSeconds = Integer.getInteger("webdriver.timeouts.implicitlywait", DEFAULT_TIMEOUT);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if ("toString".equals(method.getName())) {
            return name;
        }

        final LocalTime end = LocalTime.now().plusSeconds(timeOutInSeconds);

        StaleElementReferenceException lastException;
        do {
            try {
                return super.invoke(o, method, objects);
            } catch (StaleElementReferenceException e) {
                lastException = e;
                this.waitFor();
            }
        }
        while (LocalTime.now().isBefore(end));
        throw lastException;
    }

    protected long sleepFor() {
        return 500L;
    }

    private void waitFor() throws InterruptedException {
        Thread.sleep(this.sleepFor());
    }
}
