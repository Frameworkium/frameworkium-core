package com.frameworkium.core.ui.driver;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.driver.drivers.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Capabilities;

import java.lang.reflect.InvocationTargetException;

public class DriverSetup {

    public static final Browser DEFAULT_BROWSER = Browser.CHROME;

    /** Supported drivers. */
    public enum Browser {
        FIREFOX, CHROME, EDGE, IE, SAFARI, CUSTOM
    }

    /** Supported remote grids. */
    private enum RemoteGrid { GRID }

    protected static final Logger logger = LogManager.getLogger();

    /**
     * @return An uninitialised desired {@link Driver} implementation.
     */
    public static Driver instantiateDriver() {
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
    private static Driver instantiateDesiredRemote(Driver driver) {

        Capabilities capabilities = driver.getCapabilities();
        if (getRemoteType() == RemoteGrid.GRID) {
            return new GridImpl(capabilities);
        }
        return driver;
    }

    private static Driver createDriverImpl(Browser browser) {
        switch (browser) {
            case FIREFOX:
                return new FirefoxImpl();
            case CHROME:
                return new ChromeImpl();
            case EDGE:
                return new EdgeImpl();
            case IE:
                return new InternetExplorerImpl();
            case SAFARI:
                return new SafariImpl();
            case CUSTOM:
                String customBrowserImpl = Property.CUSTOM_BROWSER_IMPL.getValue();
                try {
                    return getCustomBrowserImpl(customBrowserImpl)
                            .getDeclaredConstructor()
                            .newInstance();
                } catch (InstantiationException | IllegalAccessException
                        | NoSuchMethodException | InvocationTargetException e) {
                    throw new IllegalArgumentException(
                            "Unable to use custom browser implementation - " + customBrowserImpl, e);
                }
            default:
                throw new IllegalArgumentException("Invalid Browser specified");
        }
    }

    public static boolean useRemoteDriver() {
        return Property.GRID_URL.isSpecified();
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
        return RemoteGrid.GRID;
    }

    /**
     * Returns custom Driver implementation based on (full) class name.
     *
     * @param implClassName fully qualified name of custom browser impl class
     * @return Class implementing the Driver interface
     */
    private static Class<? extends Driver> getCustomBrowserImpl(String implClassName) {
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
