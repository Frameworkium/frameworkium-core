package com.frameworkium.core.ui.driver;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.driver.drivers.*;
import com.frameworkium.core.ui.driver.remotes.BrowserStack;
import com.frameworkium.core.ui.driver.remotes.Sauce;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Capabilities;

import java.lang.reflect.InvocationTargetException;

public class DriverSetup {

    public static final Browser DEFAULT_BROWSER = Browser.FIREFOX;

    /** Supported drivers. */
    public enum Browser {
        FIREFOX, LEGACYFIREFOX, CHROME, IE, SAFARI, ELECTRON, CUSTOM
    }

    /** Supported remote grids. */
    private enum RemoteGrid {
        SAUCE, BROWSERSTACK, GRID
    }

    /** Supported platforms for remote grids. */
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
        driver.initialise();
        return driver;
    }

    /**
     * Uses parameters to determine which browser/remote/platform to use.
     *
     * @param driver the desired (non-remote) driver implementation
     * @return The (potentially) remote driver implementation based on parameters
     */
    private Driver instantiateDesiredRemote(Driver driver) {

        Capabilities capabilities = driver.getCapabilities();
        Platform platform = getPlatformType();
        switch (getRemoteType()) {
            case SAUCE:
                return new SauceImpl(platform, capabilities);
            case BROWSERSTACK:
                return new BrowserStackImpl(platform, capabilities);
            case GRID:
                return new GridImpl(capabilities);
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
            case IE:
                return new InternetExplorerImpl();
            case SAFARI:
                return new SafariImpl();
            case ELECTRON:
                return new ElectronImpl();
            case CUSTOM:
                String customBrowserImpl = Property.CUSTOM_BROWSER_IMPL.getValue();
                try {
                    return getCustomBrowserImpl(customBrowserImpl)
                            .getDeclaredConstructor()
                            .newInstance();
                } catch (InstantiationException | IllegalAccessException
                        | NoSuchMethodException | InvocationTargetException e) {
                    throw new RuntimeException(
                            "Unable to use custom browser implementation - " + customBrowserImpl, e);
                }
            default:
                throw new IllegalArgumentException("Invalid Browser specified");
        }
    }

    public static boolean useRemoteDriver() {
        return Property.GRID_URL.isSpecified()
                || Sauce.isDesired()
                || BrowserStack.isDesired();
    }

    private static Platform getPlatformType() {
        if (Property.PLATFORM.isSpecified()) {
            return Platform.valueOf(Property.PLATFORM.getValue().toUpperCase());
        } else {
            return Platform.NONE;
        }
    }

    private static Browser getBrowserTypeFromProperty() {
        if (Property.CUSTOM_BROWSER_IMPL.isSpecified()) {
            return Browser.CUSTOM;
        } else if (Property.BROWSER.isSpecified()) {
            return Browser.valueOf(Property.BROWSER.getValue().toUpperCase());
        } else {
            return DEFAULT_BROWSER;
        }
    }

    private static RemoteGrid getRemoteType() {
        if (Sauce.isDesired()) {
            return RemoteGrid.SAUCE;
        } else if (BrowserStack.isDesired()) {
            return RemoteGrid.BROWSERSTACK;
        } else {
            return RemoteGrid.GRID;
        }
    }

    /**
     * Returns custom Driver implementation based on (full) class name.
     *
     * @param implClassName fully qualified name of custom browser impl class
     * @return Class implementing the Driver interface
     */
    private Class<? extends Driver> getCustomBrowserImpl(String implClassName) {
        try {
            return Class.forName(implClassName).asSubclass(Driver.class);
        } catch (ClassNotFoundException ex) {
            String message = "Failed to find custom browser implementation class: " + implClassName;
            logger.fatal(message, ex);
            throw new IllegalArgumentException(message
                    + "\nFully qualified class name is required. "
                    + "e.g. com.frameworkium.ui.MyCustomImpl");
        } catch (ClassCastException ex) {
            String message = String.format(
                    "Custom browser implementation class '%s' does not implement the Driver interface.",
                    implClassName);
            logger.fatal(message, ex);
            throw new IllegalArgumentException(message, ex);
        }
    }
}
