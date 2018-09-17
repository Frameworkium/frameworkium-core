package com.frameworkium.core.ui.driver;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.proxy.SeleniumProxyFactory;
import com.frameworkium.core.ui.capture.ScreenshotCapture;
import com.frameworkium.core.ui.driver.remotes.BrowserStack;
import com.frameworkium.core.ui.driver.remotes.Sauce;
import com.frameworkium.core.ui.listeners.CaptureListener;
import com.frameworkium.core.ui.listeners.LoggingListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.ImmutableCapabilities;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import static java.util.concurrent.TimeUnit.SECONDS;

public abstract class AbstractDriver implements Driver {

    protected static final Logger logger = LogManager.getLogger();

    private EventFiringWebDriver webDriverWrapper;

    @Override
    public EventFiringWebDriver getWebDriver() {
        return this.webDriverWrapper;
    }

    /**
     * Creates the Wrapped Driver object and maximises if required.
     */
    public void initialise() {
        this.webDriverWrapper = setupEventFiringWebDriver(getCapabilities());
        maximiseBrowserIfRequired();
    }

    private Capabilities addProxyIfRequired(Capabilities caps) {
        if (Property.PROXY.isSpecified()) {
            return caps.merge(createProxyCapabilities(Property.PROXY.getValue()));
        } else {
            return caps;
        }
    }

    private EventFiringWebDriver setupEventFiringWebDriver(Capabilities capabilities) {
        Capabilities caps = addProxyIfRequired(capabilities);
        logger.debug("Browser Capabilities: " + caps);
        EventFiringWebDriver eventFiringWD = new EventFiringWebDriver(getWebDriver(caps));
        eventFiringWD.register(new LoggingListener());
        if (ScreenshotCapture.isRequired()) {
            eventFiringWD.register(new CaptureListener());
        }
        if (!Driver.isNative()) {
            eventFiringWD.manage().timeouts().setScriptTimeout(10, SECONDS);
        }
        return eventFiringWD;
    }

    private void maximiseBrowserIfRequired() {
        if (isMaximiseRequired()) {
            this.webDriverWrapper.manage().window().maximize();
        }
    }

    private boolean isMaximiseRequired() {
        boolean ableToMaximise = !Sauce.isDesired()
                && !BrowserStack.isDesired()
                && !Driver.isNative();

        return ableToMaximise && Property.MAXIMISE.getBoolean();
    }

    private static Capabilities createProxyCapabilities(String proxyProperty) {
        return new ImmutableCapabilities(
                CapabilityType.PROXY,
                SeleniumProxyFactory.createProxy(proxyProperty));
    }

}
