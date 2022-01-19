package com.frameworkium.lite.ui.driver.lifecycle;

import com.frameworkium.lite.ui.driver.Driver;
import org.openqa.selenium.WebDriver;

import java.util.function.Supplier;

/**
 * Controls the lifecycle of the {@link Driver}(s).
 *
 * <p>The methods need to be called in order:
 * <ol>
 * <li>{@link #initDriverPool()}
 * (only once until {@link #tearDownDriverPool()} has been called)
 * <li>{@link #initBrowserBeforeTest()}
 * (only once until {@link #tearDownDriver()} has been called)
 * <li>{@link #getWebDriver()}
 * (n times, but only after the above, and before the below)
 * <li>{@link #tearDownDriver()}
 * (once after {@link #initBrowserBeforeTest()} has been called)
 * <li>{@link #tearDownDriverPool()}
 * (once but multiple calls do nothing)
 * </ol>
 */
public interface DriverLifecycle {

    /**
     * Will initialise a pool of {@link Driver}s if required.
     *
     * @throws IllegalStateException if trying to re-initialise existing pool
     */
    default void initDriverPool() {}

    /**
     * Will set the current {@link ThreadLocal} {@link Driver} to be the next
     * available from the pool or will add the {@link Driver} created by the
     * supplied {@link Supplier}.
     *
     * @throws java.util.NoSuchElementException if this pool is empty
     */
    void initBrowserBeforeTest();

    /**
     * @return the {@link WebDriver} in use by the current thread.
     * @throws NullPointerException if called before
     *                              {@link #initBrowserBeforeTest()} or
     *                              after {@link #tearDownDriver()}.
     */
    WebDriver getWebDriver();

    /**
     * Tears down the driver, ready for reinitialisation, if required, by
     * {@link #initBrowserBeforeTest()}.
     */
    void tearDownDriver();

    /**
     * Clears the driver pool, if exists, ready to run
     * {@link #initDriverPool()} again if required.
     */
    default void tearDownDriverPool() {}

    /**
     * Re-initialises the browser for the current thread.
     * This can be useful if the browser crashes or becomes unreachable,
     * and you don't want to restart the whole test suite.
     */
    void reinitialiseCurrentDriver();
}
