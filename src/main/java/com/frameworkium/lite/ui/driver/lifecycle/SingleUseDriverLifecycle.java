package com.frameworkium.lite.ui.driver.lifecycle;

import com.frameworkium.lite.ui.driver.Driver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.util.function.Supplier;

/**
 * {@link #initDriverPool()} and {@link #tearDownDriverPool()} do not do
 * anything for {@link SingleUseDriverLifecycle} and can be omitted.
 *
 * @see DriverLifecycle
 */
public class SingleUseDriverLifecycle implements DriverLifecycle {

    private static final Logger logger = LogManager.getLogger();

    private static final ThreadLocal<Driver> threadLocalDriver = new ThreadLocal<>();

    private final Supplier<Driver> driverSupplier;

    public SingleUseDriverLifecycle(Supplier<Driver> driverSupplier) {
        this.driverSupplier = driverSupplier;
    }

    /**
     * Sets the {@link Driver} created by the supplied {@link Supplier} to the
     * {@link ThreadLocal} driver.
     */
    @Override
    public void initBrowserBeforeTest() {
        threadLocalDriver.set(driverSupplier.get());
    }

    @Override
    public WebDriver getWebDriver() {
        return threadLocalDriver.get().getWebDriver();
    }

    /** Calls {@code quit()} on the underlying driver. */
    @Override
    public void tearDownDriver() {
        try {
            threadLocalDriver.get().getWebDriver().quit();
        } catch (Exception e) {
            logger.warn("Failed to quit browser.");
            logger.debug("Failed to quit browser", e);
        } finally {
            threadLocalDriver.remove();
        }
    }

    @Override
    public void reinitialiseCurrentDriver() {
        try {
            threadLocalDriver.get().getWebDriver().quit();
        } catch (Exception e) {
            logger.debug("Failed to quit current driver during reinitialise.", e);
        }
    }
}

