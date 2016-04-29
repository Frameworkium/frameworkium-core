package com.frameworkium.core.ui.driver;

import com.frameworkium.core.common.properties.CommonProperty;
import com.frameworkium.core.ui.capture.ScreenshotCapture;
import com.frameworkium.core.ui.listeners.CaptureListener;
import com.frameworkium.core.ui.listeners.EventListener;
import com.frameworkium.core.ui.properties.UIProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import static com.frameworkium.core.ui.driver.DriverSetup.useRemoteDriver;

public abstract class DriverType {

    protected WebDriverWrapper webDriverWrapper;

    protected final static Logger logger = LogManager.getLogger(DriverType.class);
    private static final String HOSTNAME_OR_IP_AND_PORT_REGEX = "[\\dA-Za-z.:%-]+";

    /**
     * Creates the Wrapped Driver object, and returns to the test
     */
    public void instantiate() {
        logger.info("Current Browser Selection: " + this);

        DesiredCapabilities caps = getDesiredCapabilities();

        Proxy currentProxy = getProxy();
        if (currentProxy != null) {
            caps.setCapability(CapabilityType.PROXY, currentProxy);
        }

        logger.info("Caps: " + caps.toString());

        WebDriverWrapper eventFiringWD = new WebDriverWrapper(getWebDriverObject(caps));
        eventFiringWD.register(new EventListener());
        if (ScreenshotCapture.isRequired()) {
            eventFiringWD.register(new CaptureListener());
        }
        this.webDriverWrapper = eventFiringWD;
    }


    //TODO _ TEMP TEMP TEMP!
    public WebDriverWrapper instantiate2(DesiredCapabilities caps) {
        logger.info("Current Browser Selection: " + this);

        Proxy currentProxy = getProxy();
        if (currentProxy != null) {
            caps.setCapability(CapabilityType.PROXY, currentProxy);
        }

        logger.info("Caps: " + caps.toString());

        WebDriverWrapper eventFiringWD = new WebDriverWrapper(getWebDriverObject(caps));
        eventFiringWD.register(new EventListener());
        if (ScreenshotCapture.isRequired()) {
            eventFiringWD.register(new CaptureListener());
        }
        return eventFiringWD;
    }



    /**
     * This method returns a proxy object with settings set by the system properties. If no valid proxy argument is set
     * then it returns null.
     *
     * @return A Selenium proxy object for the current system properties or null if no valid proxy settings
     */
    public Proxy getProxy() {
        if (CommonProperty.PROXY.isSpecified()) {
            Proxy proxy = new Proxy();
            String proxyString = CommonProperty.PROXY.getValue().toLowerCase();
            switch (proxyString) {

                case "system":
                    proxy.setProxyType(ProxyType.SYSTEM);
                    logger.info("Using system proxy");
                    break;
                case "autodetect":
                    proxy.setProxyType(ProxyType.AUTODETECT);
                    logger.info("Using autodetected proxy");
                    break;
                case "direct":
                    proxy.setProxyType(ProxyType.DIRECT);
                    logger.info("Using direct (no) proxy");
                    break;
                default:
                    proxy.setProxyType(ProxyType.MANUAL);
                    if (verifyProxyAddress(proxyString)) {
                        proxy.setHttpProxy(proxyString).setFtpProxy(proxyString).setSslProxy(proxyString);
                        String logMessage = String
                                .format("Set all protocols to use proxy with address %s", proxyString);
                        logger.info(logMessage);
                    } else {
                        logger.error("Invalid proxy setting specified, acceptable values are: system, autodetect, direct or {hostname}:{port}. Tests will now use default setting for your browser");
                        return null;
                    }
                    break;
            }
            return proxy;
        }
        return null;
    }

    /**
     * This helper method verifies that a value is suitable for usage as a proxy address. Selenium expects values of the
     * format hostname:port or ip:port
     *
     * @param proxyAddress The proxy value to verify
     * @return true if value is acceptable as a proxy, false otherwise
     */
    private boolean verifyProxyAddress(final String proxyAddress) {
        return proxyAddress.matches(HOSTNAME_OR_IP_AND_PORT_REGEX);
    }

    /**
     * Returns the WebDriverWrapper with the initialised driver inside
     *
     * @return - Initialised WebDriverWrapper
     */
    public WebDriverWrapper getDriver() {
        return this.webDriverWrapper;
    }

    /**
     * Determines whether tests have been ran to configure against a mobile
     *
     * @return - Boolean, whether using mobile or not
     */
    public static boolean isMobile() {
        return false;
    }

    /**
     * Maximises the browser window based on parameters
     */
    public void maximiseBrowserWindow() {
        if (!UIProperty.MAXIMISE.isSpecified() || Boolean.parseBoolean(UIProperty.MAXIMISE.getValue())) {
            if (!useRemoteDriver() && !isNative() || UIProperty.GRID_URL.isSpecified()) {
                this.webDriverWrapper.manage().window().maximize();
            }
        }
    }

    /**
     * Method to tear down the driver object, can be overridden
     */
    public void tearDownDriver() {
        this.webDriverWrapper.quit();
    }

    /**
     * Reset the browser based on whether it's been reset before
     */
    public boolean resetBrowser(final boolean requiresReset) {
        if (requiresReset) {
            tearDownDriver();
            instantiate();
        }
        return true;
    }

    /**
     * Determines whether a native app is being used for testing
     *
     * @return - Boolean, to whether an native app is defined
     */
    public static boolean isNative() {
        return UIProperty.APP_PATH.isSpecified();
    }

    /**
     * Implemented in each Driver Type to specify the capabilities of that browser
     *
     * @return - Desired Capabilities of each browser
     */
    public abstract DesiredCapabilities getDesiredCapabilities();

    /**
     * Returns the correct WebDriver object for that driver type
     *
     * @param capabilities - Capabilities of the browser
     * @return - WebDriver object for the browser
     */
    public abstract WebDriver getWebDriverObject(DesiredCapabilities capabilities);

}
