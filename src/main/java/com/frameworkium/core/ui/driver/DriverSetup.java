package com.frameworkium.core.ui.driver;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.driver.drivers.*;
import com.frameworkium.core.ui.driver.remotes.BrowserStack;
import com.frameworkium.core.ui.driver.remotes.Sauce;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.reflections.Reflections;

import java.util.Set;

public class DriverSetup {

    public static final Browser DEFAULT_BROWSER = Browser.FIREFOX;

    /** Supported drivers */
    public enum Browser {
        FIREFOX, LEGACYFIREFOX, CHROME, OPERA, IE, PHANTOMJS, SAFARI, ELECTRON, CUSTOM
    }

    /** Supported remote grids */
    private enum RemoteGrid {
        SAUCE, BROWSERSTACK, GRID
    }

    /** Supported platforms for remote grids */
    public enum Platform {
        WINDOWS, OSX, IOS, ANDROID, NONE
    }

    protected static final Logger logger = LogManager.getLogger();

    /**
     * @return An uninitialised desired {@link Driver} Implementation.
     */
    public Driver instantiateDriver() {
        Driver driver = createDriverImpl(getBrowserTypeFromProperty());
        if (useRemoteDriver()) {
            driver = instantiateDesiredRemote(driver);
        }
        return driver;
    }

    /**
     * Uses parameters to determine which browser/remote/platform to use
     *
     * @param driver the desired (non-remote) driver implementation
     * @return The (potentially) remote driver implementation based on parameters
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
            case FIREFOX:
                return new FirefoxImpl();
            case LEGACYFIREFOX:
                return new LegacyFirefoxImpl();
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
            case CUSTOM:
                try {
                    return getCustomBrowserImpl(Property.BROWSER.getValue()).newInstance();
                } catch (Exception e) {
                    throw new IllegalArgumentException(String.format("Invalid custom browser type"));
                }
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
            try {
                return Browser.valueOf(Property.BROWSER.getValue().toUpperCase());
            } catch (IllegalArgumentException e) {
                return Browser.CUSTOM;
            }
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

    /**
     * Returns custom AbstractDriver implementation based on string input. Uses reflections library to find options
     *
     * @param implClassName the name of custom browser impl class (simpleName, not full path)
     * @return Class implementing AbstractDriver interface
     */
    private static Class<? extends AbstractDriver> getCustomBrowserImpl(String implClassName) {
        Reflections reflections = new Reflections("");
        Set<Class<? extends AbstractDriver>> customDriverImpls = reflections.getSubTypesOf(AbstractDriver.class);
        return customDriverImpls.stream()
                .filter(s -> s.getSimpleName().equals(implClassName))
                .findFirst()
                .get();
    }
}
