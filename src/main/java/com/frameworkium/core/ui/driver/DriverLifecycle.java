package com.frameworkium.core.ui.driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * Controls the lifecycle of the {@link Driver}(s).
 *
 * <p>The methods need to be called in order:
 * <ol>
 * <li>{@link #initDriverPool(Supplier)}
 * (once until {@link #tearDownDroverPool()} has been called)
 * <li>{@link #initBrowserBeforeTest(Supplier)}
 * (once until {@link #tearDownDriver()} has been called)
 * <li>{@link #getWebDriver()} (n times only after the above and before the below)
 * <li>{@link #tearDownDriver()}
 * (once after {@link #initBrowserBeforeTest(Supplier)} has been called)
 * <li>{@link #tearDownDroverPool()} (once but multiple calls do nothing)
 * </ol>
 */
public class DriverLifecycle {

    private static final Logger logger = LogManager.getLogger();

    private boolean reuseBrowser;
    private int poolSize;

    private BlockingDeque<Driver> driverPool;
    private static final ThreadLocal<Driver> driver = ThreadLocal.withInitial(() -> null);

    public DriverLifecycle(int poolSize, boolean reuseBrowser) {
        this.poolSize = poolSize;
        this.reuseBrowser = reuseBrowser;
    }

    /**
     * If reuseBrowser is set this will, in parallel, create {@link Driver}s and
     * add them to the pool up to the number of threads specified.
     *
     * @param driverSupplier the {@link Supplier} that creates {@link Driver}s
     */
    public void initDriverPool(Supplier<Driver> driverSupplier) {
        if (driverPool != null) {
            throw new IllegalStateException(
                    "initDriverPool called when already initialised");
        }
        if (reuseBrowser) {
            driverPool = new LinkedBlockingDeque<>(poolSize);
            IntStream.range(0, poolSize)
                    .parallel()
                    .mapToObj(i -> driverSupplier.get())
                    .forEach(driverPool::addLast);
        }
    }

    /**
     * Will set the current {@link ThreadLocal} {@link Driver} to be the next
     * available from the pool if reuseBrowser is true, or else will add the
     * {@link Driver} created by the supplied {@link Supplier}.
     *
     * @param driverSupplier the {@link Supplier} that creates {@link Driver}s
     * @throws java.util.NoSuchElementException if this pool is empty
     */
    public void initBrowserBeforeTest(Supplier<Driver> driverSupplier) {
        if (reuseBrowser) {
            driver.set(getNextAvailableDriverFromPool());
        } else {
            driver.set(driverSupplier.get());
        }
    }

    /**
     * The pool should not be empty here. It has been initialised with one driver
     * per thread and each driver is returned to the pool upon test completion.
     *
     * @return the next available {@link Driver}
     * @throws java.util.NoSuchElementException if this pool is empty
     */
    private Driver getNextAvailableDriverFromPool() {
        return driverPool.removeFirst();
    }

    /**
     * @return the driver in use by the current thread.
     */
    public WebDriver getWebDriver() {
        return driver.get().getWebDriver();
    }

    /**
     * If reuseBrowser is true, this will {@code }deleteAllCookies} and then
     * re-add the {@link Driver} back to the pool, else it will call {@code quit()}
     * on the underlying {@link WebDriver}.
     */
    public void tearDownDriver() {
        try {
            WebDriver webDriver = driver.get().getWebDriver();
            if (reuseBrowser) {
                webDriver.manage().deleteAllCookies();
                driverPool.addLast(driver.get());
            } else {
                webDriver.quit();
            }
        } catch (Exception e) {
            logger.error("Failed to tear down browser after test method.", e);
        }
    }

    /**
     * Drains the pool, calls {@link WebDriver#quit} on every {@link Driver}
     * remaining in the pool and null the pool.
     */
    public void tearDownDroverPool() {
        if (driverPool == null) {
            return;
        }

        Collection<Driver> driversToQuit = new ArrayList<>();

        driverPool.drainTo(driversToQuit);
        driverPool = null; // allows re-initialisation of this DriverLifecycle

        driversToQuit.parallelStream()
                .forEach(driver -> {
                    try {
                        driver.getWebDriver().quit();
                    } catch (Exception e) {
                        logger.error("Failed to tear down browser after test suite.", e);
                    }
                });
    }
}

