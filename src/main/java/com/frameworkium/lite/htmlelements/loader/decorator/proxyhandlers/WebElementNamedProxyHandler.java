package com.frameworkium.lite.htmlelements.loader.decorator.proxyhandlers;

import com.frameworkium.lite.htmlelements.utils.HtmlElementUtils;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.internal.LocatingElementHandler;

import java.lang.reflect.Method;
import java.time.Clock;
import java.util.concurrent.TimeUnit;

public class WebElementNamedProxyHandler extends LocatingElementHandler {

    private final long timeOutInSeconds;
    private final Clock clock;
    private final String name;

    public WebElementNamedProxyHandler(ElementLocator locator, String name) {
        super(locator);
        this.name = name;
        this.clock = Clock.systemDefaultZone();
        this.timeOutInSeconds = HtmlElementUtils.getImplicitTimeoutInSeconds();
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if ("toString".equals(method.getName())) {
            return name;
        }

        final long end = this.clock.millis() + TimeUnit.SECONDS.toMillis(this.timeOutInSeconds);

        StaleElementReferenceException lastException;
        do {
            try {
                return super.invoke(o, method, objects);
            } catch (StaleElementReferenceException e) {
                lastException = e;
                this.waitFor();
            }
        }
        while (this.clock.millis() < end);
        throw lastException;
    }

    protected long sleepFor() {
        return 500L;
    }

    private void waitFor() throws InterruptedException {
        Thread.sleep(this.sleepFor());
    }
}
