package com.frameworkium.core.ui.driver;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.capture.ScreenshotCapture;
import com.frameworkium.core.ui.driver.remotes.BrowserStack;
import com.frameworkium.core.ui.driver.remotes.Sauce;
import com.frameworkium.core.ui.listeners.CaptureListener;
import com.frameworkium.core.ui.listeners.LoggingListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.remote.CapabilityType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import static com.frameworkium.core.ui.tests.BaseTest.DEFAULT_TIMEOUT_SECONDS;

public abstract class AbstractDriver implements Driver {

    protected static final Logger logger = LogManager.getLogger();

    private WebDriverWrapper webDriverWrapper;

    @Override
    public void tearDown() {
        if (Property.REUSE_BROWSER.isSpecified()) {
            webDriverWrapper.manage().deleteAllCookies();
        } else {
            webDriverWrapper.quit();
        }
    }

    @Override
    public WebDriverWrapper getDriver() {
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
            MutableCapabilities mutableCapabilities = new MutableCapabilities();
            mutableCapabilities.setCapability(CapabilityType.PROXY, getProxy());
            return caps.merge(mutableCapabilities);
        } else {
            return caps;
        }
    }

    private WebDriverWrapper setupEventFiringWebDriver(Capabilities capabilities) {
        Capabilities caps = addProxyIfRequired(capabilities);
        logger.debug("Browser Capabilities: " + caps);
        WebDriverWrapper eventFiringWD = new WebDriverWrapper(getWebDriver(caps));
        eventFiringWD.register(new LoggingListener());
        if (ScreenshotCapture.isRequired()) {
            eventFiringWD.register(new CaptureListener());
        }
        if (!Driver.isNative()) {
            eventFiringWD.manage().timeouts().setScriptTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
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

        return Property.wantToMaximise() && ableToMaximise;
    }

    /**
     * Get proxy object with settings set by the system properties.
     * Requires Property.PROXY.isSpecified()
     *
     * @return A Selenium proxy object for the current system properties
     */
    private Proxy getProxy() {
        Proxy proxy = new Proxy();
        String proxyString = Property.PROXY.getValue().toLowerCase();
        switch (proxyString) {
            case "system":
                proxy.setProxyType(ProxyType.SYSTEM);
                logger.debug("Using system proxy");
                break;
            case "autodetect":
                proxy.setProxyType(ProxyType.AUTODETECT);
                logger.debug("Using autodetect proxy");
                break;
            case "direct":
                proxy.setProxyType(ProxyType.DIRECT);
                logger.debug("Using direct i.e. (no) proxy");
                break;
            default:
                try {
                    URI proxyURI = new URI(Property.PROXY.getValue());
                    proxyString = String.format("%s:%d", proxyURI.getHost(), proxyURI.getPort());
                    proxy.setProxyType(ProxyType.MANUAL)
                            .setHttpProxy(proxyString)
                            .setFtpProxy(proxyString)
                            .setSslProxy(proxyString);
                    logger.debug("Set all protocols to use proxy address: {}", proxyString);
                } catch (URISyntaxException e) {
                    String message = "Invalid proxy specified, acceptable values are: " +
                            "system, autodetect, direct or http://{hostname}:{port}.";
                    logger.error(message);
                    throw new IllegalArgumentException(message);
                }
                break;
        }
        return proxy;
    }

}
