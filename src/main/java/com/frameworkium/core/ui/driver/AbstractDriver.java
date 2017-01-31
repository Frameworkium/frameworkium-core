package com.frameworkium.core.ui.driver;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.capture.ScreenshotCapture;
import com.frameworkium.core.ui.driver.remotes.BrowserStack;
import com.frameworkium.core.ui.driver.remotes.Sauce;
import com.frameworkium.core.ui.listeners.CaptureListener;
import com.frameworkium.core.ui.listeners.EventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.concurrent.TimeUnit;

public abstract class AbstractDriver implements Driver {

    protected static final Logger logger = LogManager.getLogger();

    private static final String HOSTNAME_OR_IP_PORT_REGEX = "[\\dA-Za-z\\.\\-]+:[\\d]+";
    private WebDriverWrapper webDriverWrapper;

    /** {@inheritDoc} */
    @Override
    public void tearDown() {
        this.webDriverWrapper.quit();
    }

    /** {@inheritDoc} */
    @Override
    public WebDriverWrapper getDriver() {
        return this.webDriverWrapper;
    }

    /** Creates the Wrapped Driver object and maximises if required. */
    public void initialise() {
        DesiredCapabilities capsFromImpl = getDesiredCapabilities();
        DesiredCapabilities caps = addProxyIfRequired(capsFromImpl);
        logger.debug("Browser Capabilities: " + caps);

        this.webDriverWrapper = setupEventFiringWebDriver(caps);

        maximiseBrowserIfRequired();
    }

    private DesiredCapabilities addProxyIfRequired(DesiredCapabilities caps) {
        Proxy currentProxy = getProxy();
        if (currentProxy != null) {
            caps.setCapability(CapabilityType.PROXY, currentProxy);
        }
        return caps;
    }

    private WebDriverWrapper setupEventFiringWebDriver(DesiredCapabilities caps) {
        WebDriverWrapper eventFiringWD = new WebDriverWrapper(getWebDriver(caps));
        eventFiringWD.register(new EventListener());
        if (ScreenshotCapture.isRequired()) {
            eventFiringWD.register(new CaptureListener());
        }
        // TODO: allow parametrisation
        eventFiringWD.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
        return eventFiringWD;
    }

    /** Maximises the browser window based on maximise property */
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
     * This method returns a proxy object with settings set by the system properties.
     * If no valid proxy argument is set then it returns null.
     *
     * @return A Selenium proxy object for the current system properties
     * or null if no valid proxy settings
     */
    private Proxy getProxy() {
        if (!Property.PROXY.isSpecified()) {
            return null;
        }

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
                // assumed to be a proxy url
                if (isProxyAddressWellFormed(proxyString)) {
                    proxy.setProxyType(ProxyType.MANUAL)
                            .setHttpProxy(proxyString)
                            .setFtpProxy(proxyString)
                            .setSslProxy(proxyString);
                    logger.debug("Set all protocols to use proxy address: " + proxyString);
                } else {
                    logger.error("Invalid proxy specified, acceptable values are: "
                            + "system, autodetect, direct or {hostname}:{port}. "
                            + "Tests will use default setting for your browser");
                    return null;
                }
                break;
        }
        return proxy;
    }

    /**
     * This helper method verifies that a value is a suitable proxy address.
     * Selenium expects values of the format hostname:port or ip:port
     *
     * @param proxyAddress The proxy value to verify
     * @return true if value is acceptable as a proxy, false otherwise
     */
    private boolean isProxyAddressWellFormed(String proxyAddress) {
        return proxyAddress.matches(HOSTNAME_OR_IP_PORT_REGEX);
    }

}
