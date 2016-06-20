package com.frameworkium.core.ui.driver;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.driver.drivers.*;
import com.frameworkium.core.ui.driver.remotes.BrowserStack;
import com.frameworkium.core.ui.driver.remotes.Sauce;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.remote.DesiredCapabilities;

public class DriverSetup {

    public static final Browser DEFAULT_BROWSER = Browser.FIREFOX;

    /** Supported drivers */
    public enum Browser {
        MARIONETTE, FIREFOX, CHROME, OPERA, IE, PHANTOMJS, SAFARI, ELECTRON
    }

    /** Supported remote grids */
    private enum RemoteGrid {
        SAUCE, BROWSERSTACK, GRID
    }

    /** Supported platforms for remote grids */
    public enum Platform {
        WINDOWS, OSX, IOS, ANDROID, NONE
    }

    protected final static Logger logger = LogManager.getLogger(DriverSetup.class);

    /**
     * Returns the initialises driver.
     *
     * @return A {@link Driver} Implementation
     */
    public Driver instantiateDriver() {
        Browser browser = getBrowserTypeFromProperty();
        Driver driver = createDriverImpl(browser);
        if (useRemoteDriver()) {
            driver = instantiateDesiredRemote(driver);
        }
        driver.initialise();
        return driver;
    }

    /**
     * Uses the parameters given to determine which browser/remote/platform to use
     *
     * @return The correct driver type based on parameters
     */
    private Driver instantiateDesiredRemote(Driver driver) {

        DesiredCapabilities desiredCapabilities = driver.getDesiredCapabilities();
        Platform platform = getPlatformType();
        switch (returnRemoteType()) {
            case SAUCE:
                return new SauceImpl(platform, desiredCapabilities);
            case BROWSERSTACK:
                return new BrowserStackImpl(platform, desiredCapabilities);
            case GRID:
                return new GridImpl(desiredCapabilities);
            default:
                return driver;
        }
    }

    private Driver createDriverImpl(Browser browser) {
        switch (browser) {
            case MARIONETTE:
                return new MarionetteImpl();
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
            default:
                throw new IllegalArgumentException("Invalid browser type.");
        }
    }

    /**
     * Checks whether a remote driver is wanting to be used
     *
     * @return True/False to whether to use a remote driver
     */
    public static boolean useRemoteDriver() {
        return Property.GRID_URL.isSpecified()
                || Sauce.isDesired()
                || BrowserStack.isDesired();
    }

    /**
     * Returns the platform type, if it's been given
     *
     * @return Platform type
     */
    private static Platform getPlatformType() {
        if (Property.PLATFORM.isSpecified()) {
            return Platform.valueOf(Property.PLATFORM.getValue().toUpperCase());
        } else {
            return Platform.NONE;
        }
    }

    /**
     * Returns the browser type and returns default if not specified
     *
     * @return Browser Type
     */
    private static Browser getBrowserTypeFromProperty() {
        if (!Property.BROWSER.isSpecified()) {
            return DEFAULT_BROWSER;
        } else {
            return Browser.valueOf(Property.BROWSER.getValue().toUpperCase());
        }
    }

    /**
     * Returns what type of remote driver has been specified to be used
     *
     * @return Remote Driver Type
     */
    private static RemoteGrid returnRemoteType() {
        if (Sauce.isDesired()) {
            return RemoteGrid.SAUCE;
        } else if (BrowserStack.isDesired()) {
            return RemoteGrid.BROWSERSTACK;
        } else {
            return RemoteGrid.GRID;
        }
    }
}
