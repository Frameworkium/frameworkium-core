package com.frameworkium.ui.config;

import com.frameworkium.ui.config.remotes.BrowserStack;
import com.frameworkium.ui.config.remotes.Sauce;
import com.frameworkium.ui.config.drivers.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.remote.DesiredCapabilities;

public class DriverSetup {

    private static final SupportedBrowsers DEFAULT_BROWSER = SupportedBrowsers.FIREFOX;

    /**
     * List of supported drivers
     */
    public enum SupportedBrowsers {
        FIREFOX,CHROME,OPERA,IE,PHANTOMJS,SAFARI,ELECTRON
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
        WINDOWS,OSX,IOS,ANDROID,NONE
    }

    protected final static Logger logger = LogManager.getLogger(DriverType.class);

    /**
     * Returns the driver type to the base test, which initialises it
     *
     * @return - Driver Type
     */
    public DriverType returnDesiredDriverType() {
        return initialiseDesiredDriverType();
    }

    /**
     * Uses the parameters given to determine which browser/remote/platform to use
     *
     * @return - The correct driver type based on parameters
     */
    private DriverType initialiseDesiredDriverType() throws NullPointerException {
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
                return new InternetExplorerImpl();
            case PHANTOMJS:
                return new PhantomJSImpl();
            case SAFARI:
                return new SafariImpl();
            case ELECTRON:
                return new ElectronImpl();
        }
        throw new IllegalStateException("Invalid browser type.");
    }

    /**
     * Checks whether a remote driver is wanting to be used
     *
     * @return - True/False to whether to use a remote driver
     */
    public static boolean useRemoteDriver() {
        return SystemProperty.GRID_URL.isSpecified() || Sauce.isDesired() || BrowserStack.isDesired();
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
            return SupportedPlatforms.NONE;
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
