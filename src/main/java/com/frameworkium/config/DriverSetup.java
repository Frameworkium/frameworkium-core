package com.frameworkium.config;

import com.frameworkium.config.browsers.*;
import com.frameworkium.config.remotes.BrowserStack;
import com.frameworkium.config.remotes.Sauce;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.remote.DesiredCapabilities;
import sun.security.krb5.internal.crypto.Des;

import static com.frameworkium.config.SystemProperty.*;

public class DriverSetup {

    private static final SupportedBrowsers DEFAULT_BROWSER = SupportedBrowsers.FIREFOX;

    /**
     * List of supported browsers
     */
    private enum SupportedBrowsers {
        FIREFOX,CHROME,OPERA,IE,PHANTOMJS,SAFARI
    }

    /**
     * List of supported remote grids
     */
    private enum SupportedRemotes {
        SAUCE,BROWSERSTACK,GRID
    }

    /**
     * List of supported platforms on remote grids
     */
    public enum SupportedPlatforms {
        IOS,ANDROID,BROWSER
    }

    protected final static Logger logger = LogManager.getLogger(DriverType.class);

    /**
     * Returns the driver type to the base test, which initialises it
     *
     * @return - Driver Type
     */
    public static DriverType returnDesiredDriverType() {
        return initialiseDesiredDriverType();
    }

    /**
     * Uses the paramaters given to determine which browser/remote/platform to use
     *
     * @return - The correct driver type based on parameters
     */
    private static DriverType initialiseDesiredDriverType() throws NullPointerException {
        DriverType browserDriver = returnBrowserObject();
        DesiredCapabilities browserDesiredCapabilities = browserDriver.getDesiredCapabilities();
        if (useRemoteDriver()) {
            SupportedPlatforms platform = returnPlatformType();
            switch(returnRemoteType()) {
                case SAUCE:
                    return new SauceImpl(platform, browserDesiredCapabilities);
                case BROWSERSTACK:
                    return new BrowserStackImpl(platform, browserDesiredCapabilities);
                case GRID:
                    return new GridImpl(browserDesiredCapabilities);
            }
        }
        return browserDriver;
    }

    /**
     * @return the browser driver type object based on the browser passed in
     */
    private static DriverType returnBrowserObject() {
        switch (returnBrowserType()) {
            case FIREFOX:
                return new FirefoxImpl();
            case CHROME:
                return new ChromeImpl();
            case OPERA:
                return new OperaImpl();
            case IE:
                return new InternetExporerImpl();
            case PHANTOMJS:
                return new PhantomJSImpl();
            case SAFARI:
                return new SafariImpl();
        }
        return null;
    }

    /**
     * Checks whether a remote driver is wanting to be used
     *
     * @return - True/False to whether to use a remote driver
     */
    public static boolean useRemoteDriver() {
        return GRID_URL.isSpecified() || Sauce.isDesired() || BrowserStack.isDesired();
    }

    /**
     * Returns the platform type, if it's been given
     *
     * @return - Platform type
     */
    private static SupportedPlatforms returnPlatformType() {
        if (SystemProperty.PLATFORM.isSpecified()) {
            return SupportedPlatforms.valueOf(SystemProperty.PLATFORM.getValue().toUpperCase());
        }
        else {
            return null;
        }
    }

    /**
     * Returns the browser type and returns default if not specified
     *
     * @return - Browser Type
     */
    private static SupportedBrowsers returnBrowserType() {
        if (!SystemProperty.BROWSER.isSpecified()) {
           return DEFAULT_BROWSER;
        }
        else {
            return SupportedBrowsers.valueOf(SystemProperty.BROWSER.getValue().toUpperCase());
        }
    }

    /**
     * Returns what type of remote driver has been specified to be used
     *
     * @return - Remote Driver Type
     */
    private static SupportedRemotes returnRemoteType() {
        if (Sauce.isDesired()) {
            return SupportedRemotes.SAUCE;
        }
        else if (BrowserStack.isDesired()) {
            return SupportedRemotes.BROWSERSTACK;
        }
        else {
            return SupportedRemotes.GRID;
        }
    }
}
