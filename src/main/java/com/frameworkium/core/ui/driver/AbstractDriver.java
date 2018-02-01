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

import static com.frameworkium.core.ui.tests.BaseUITest.DEFAULT_TIMEOUT_SECONDS;

public abstract class AbstractDriver implements Driver {

    protected static final Logger logger = LogManager.getLogger();

    private WebDriverWrapper webDriverWrapper;

    @Override
    public void tearDown() {
        if (Property.REUSE_BROWSER.getBoolean()) {
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
            mutableCapabilities.setCapability(
                    CapabilityType.PROXY, createProxy(Property.PROXY.getValue()));
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

        return ableToMaximise && Property.MAXIMISE.getBoolean();
    }

    private static Proxy createProxy(String proxyProperty) {
        Proxy proxy = new Proxy();
        switch (proxyProperty.toLowerCase()) {
            case "system":
                logger.debug("Using system proxy");
                proxy.setProxyType(ProxyType.SYSTEM);
                break;
            case "autodetect":
                logger.debug("Using autodetect proxy");
                proxy.setProxyType(ProxyType.AUTODETECT);
                break;
            case "direct":
                logger.debug("Using direct i.e. (no) proxy");
                proxy.setProxyType(ProxyType.DIRECT);
                break;
            default:
                return createManualProxy(proxyProperty);
        }
        return proxy;
    }

    private static Proxy createManualProxy(String proxyProperty) {
        String proxyString = getProxyURL(proxyProperty);
        Proxy proxy = new Proxy();
        proxy.setProxyType(ProxyType.MANUAL)
                .setHttpProxy(proxyString)
                .setFtpProxy(proxyString)
                .setSslProxy(proxyString);
        logger.debug("All protocols to use proxy address: {}", proxyString);
        return proxy;
    }

    private static String getProxyURL(String proxyProperty) {
        try {
            URI proxyURI = new URI(proxyProperty);
            String host = proxyURI.getHost();
            int port = proxyURI.getPort();
            if (host == null || port == -1) {
                throw new URISyntaxException(
                        proxyProperty, "invalid host or port");
            }
            return String.format("%s:%d", host, port);
        } catch (NullPointerException | URISyntaxException e) {
            String message = "Invalid proxy specified, acceptable values are: "
                    + "system, autodetect, direct or http://{hostname}:{port}.";
            logger.error(message);
            throw new IllegalArgumentException(message, e);
        }
    }

}
