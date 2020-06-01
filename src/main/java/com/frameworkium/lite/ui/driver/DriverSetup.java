package com.frameworkium.lite.ui.driver;

import com.frameworkium.lite.common.properties.Property;
import com.frameworkium.lite.ui.driver.drivers.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;

public class DriverSetup {

    public static final Browser DEFAULT_BROWSER = Browser.CHROME;

    /** Supported drivers. */
    public enum Browser {
        FIREFOX, CHROME, EDGE, IE, SAFARI, CUSTOM
    }

    protected static final Logger logger = LogManager.getLogger();

    /**
     * @return An uninitialised desired {@link Driver} implementation.
     */
    public static Driver instantiateDriver() {
        Driver driver = createDriverImpl(getBrowserTypeFromProperty());
        if (useRemoteDriver()) {
            driver = new GridImpl(driver.getCapabilities());
        }
        driver.initialise();
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
