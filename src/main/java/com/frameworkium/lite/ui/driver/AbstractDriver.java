package com.frameworkium.lite.ui.driver;

import com.frameworkium.lite.common.properties.Property;
import com.frameworkium.lite.ui.capture.ScreenshotCapture;
import com.frameworkium.lite.ui.listeners.CaptureListener;
import com.frameworkium.lite.ui.listeners.LoggingListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Capabilities;
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

    private EventFiringWebDriver setupEventFiringWebDriver(Capabilities capabilities) {
        logger.debug("Browser Capabilities: " + capabilities);
        EventFiringWebDriver eventFiringWD = new EventFiringWebDriver(getWebDriver(capabilities));
        eventFiringWD.register(new LoggingListener());
        if (ScreenshotCapture.isRequired()) {
            eventFiringWD.register(new CaptureListener());
        }
        eventFiringWD.manage().timeouts().setScriptTimeout(10, SECONDS);
        return eventFiringWD;
    }

    private void maximiseBrowserIfRequired() {
        if (Property.MAXIMISE.getBoolean()) {
            this.webDriverWrapper.manage().window().maximize();
        }
    }

}
