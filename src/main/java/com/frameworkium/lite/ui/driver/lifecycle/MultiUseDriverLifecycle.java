package com.frameworkium.lite.ui.driver.lifecycle;

import com.frameworkium.lite.ui.driver.Driver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/** @see DriverLifecycle */
public class MultiUseDriverLifecycle implements DriverLifecycle {

    private static final Logger logger = LogManager.getLogger();

    private static final ThreadLocal<Driver> threadLocalDriver = new ThreadLocal<>();

    private final Supplier<Driver> driverSupplier;
    private final int poolSize;
    private BlockingDeque<Driver> driverPool;

    /**
     * @param driverSupplier the {@link Supplier} that creates {@link Driver}s
     * @param poolSize the fixed size of the pool of drivers
     */
    public MultiUseDriverLifecycle(Supplier<Driver> driverSupplier, int poolSize) {
        this.driverSupplier = driverSupplier;
        this.poolSize = poolSize;
    }

    /**
     * Create {@link Driver}s in parallel and add them to the pool up to the
     * size specified.
     *
     * @throws IllegalStateException if trying to re-initialise existing pool
     */
    @Override
    public void initDriverPool() {
        if (driverPool != null) {
            throw new IllegalStateException(
                    "initDriverPool called when already initialised");
        }
        driverPool = new LinkedBlockingDeque<>(poolSize);
        IntStream.range(0, poolSize)
                .parallel()
                .mapToObj(i -> driverSupplier.get())
                .forEach(driverPool::addLast);
    }

    /**
     * Will set the current {@link ThreadLocal} {@link Driver} to be the next
     * available from the pool.
     *
     * @throws java.util.NoSuchElementException if this pool is empty
     */
    @Override
    public void initBrowserBeforeTest() {
        threadLocalDriver.set(driverPool.removeFirst());
    }

    @Override
    public WebDriver getWebDriver() {
        return threadLocalDriver.get().getWebDriver();
    }

    /**
     * If reuseBrowser is true, this will {@code deleteAllCookies} and then
     * re-add the {@link Driver} back to the pool, else it will call {@code quit()}
     * on the underlying {@link WebDriver}.
     */
    @Override
    public void tearDownDriver() {
        try {
            Driver driver = threadLocalDriver.get();
            driver.getWebDriver().manage().deleteAllCookies();
            driverPool.addLast(driver);
        } catch (Exception e) {
            logger.warn("Failed to tear down browser after test method.");
            logger.debug("Failed to tear down browser after test method.", e);
            reinitialiseCurrentDriver();
        } finally {
            threadLocalDriver.remove();
        }
    }

    /**
     * Drains the pool, calls {@link WebDriver#quit} on every {@link Driver}
     * remaining in the pool and sets the pool to {@code null}.
     */
    @Override
    public void tearDownDriverPool() {
        if (driverPool == null) {
            return;
        }

        driverPool.parallelStream()
                .forEach(driver -> {
                    try {
                        driver.getWebDriver().quit();
                    } catch (Exception e) {
                        logger.error("Failed to quit a browser in the pool.");
                        logger.debug("Failed to quit a browser in the pool.", e);
                    }
                });

        driverPool = null; // allows re-initialisation
    }

    @Override
    public void reinitialiseCurrentDriver() {
        Driver currentDriver = threadLocalDriver.get();
        if (currentDriver != null) {
            try {
                currentDriver.getWebDriver().quit();
            } catch (Exception e) {
                logger.warn("Failed to quit existing browser in the pool.", e);
            }
        }
        threadLocalDriver.remove();
        driverPool.addLast(driverSupplier.get());
    }
}
