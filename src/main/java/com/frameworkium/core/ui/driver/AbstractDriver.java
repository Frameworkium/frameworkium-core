package com.frameworkium.core.ui.driver;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.capture.ScreenshotCapture;
import com.frameworkium.core.ui.listeners.CaptureListener;
import com.frameworkium.core.ui.listeners.EventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.concurrent.TimeUnit;

import static com.frameworkium.core.ui.driver.DriverSetup.useRemoteDriver;

public abstract class AbstractDriver implements Driver {

    protected final static Logger logger = LogManager.getLogger();
    private static final String HOSTNAME_OR_IP_AND_PORT_REGEX = "[\\dA-Za-z.:%-]+";
    private WebDriverWrapper webDriverWrapper;
    private boolean isInitialised;

    /** {@inheritDoc} */
    @Override
    public void resetBrowser() {
        tearDown();
        initialise();
    }

    /** {@inheritDoc} */
    @Override
    public void tearDown() {
        if (isInitialised) {
            this.webDriverWrapper.quit();
            isInitialised = false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public WebDriverWrapper getDriver() {
        return this.webDriverWrapper;
    }

    /** Creates the Wrapped Driver object and maximises if required. */
    private void initialise() {
        if (!isInitialised) {
            DesiredCapabilities capsFromImpl = getDesiredCapabilities();
            DesiredCapabilities caps = addProxyIfRequired(capsFromImpl);
            logger.debug("Browser Capabilities: " + caps);

            this.webDriverWrapper = setupEventFiringWebDriver(caps);

            maximiseBrowserIfRequired();
            isInitialised = true;
        }
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
    public void maximiseBrowserIfRequired() {

        boolean wantToMaximise = Property.wantToMaximise();
        boolean ableToMaximise = !useRemoteDriver() && !Driver.isNative();

        if (wantToMaximise && ableToMaximise) {
            this.webDriverWrapper.manage().window().maximize();
        }
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
                    logger.error("Invalid proxy setting specified, acceptable values are: " +
                            "system, autodetect, direct or {hostname}:{port}. " +
                            "Tests will now use default setting for your browser");
                    return null;
                }
                break;
        }
        return proxy;
    }

    /**
     * This helper method verifies that a value is suitable for usage as a proxy address.
     * Selenium expects values of the format hostname:port or ip:port
     *
     * @param proxyAddress The proxy value to verify
     * @return true if value is acceptable as a proxy, false otherwise
     */
    private boolean isProxyAddressWellFormed(String proxyAddress) {
        return proxyAddress.matches(HOSTNAME_OR_IP_AND_PORT_REGEX);
    }

}
