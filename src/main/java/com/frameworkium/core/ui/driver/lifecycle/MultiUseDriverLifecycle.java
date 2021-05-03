package com.frameworkium.core.ui.driver.lifecycle;

import com.frameworkium.core.ui.driver.Driver;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

/**
 * @see DriverLifecycle
 */
public class MultiUseDriverLifecycle implements DriverLifecycle {

  private static final Logger logger = LogManager.getLogger();
  private static final ThreadLocal<Driver> threadLocalDriver = new ThreadLocal<>();
  private int poolSize;
  private BlockingDeque<Driver> driverPool;

  public MultiUseDriverLifecycle(int poolSize) {
    this.poolSize = poolSize;
  }

  /**
   * Create {@link Driver}s in parallel and add them to the pool up to the
   * size specified.
   *
   * @param driverSupplier the {@link Supplier} that creates {@link Driver}s
   * @throws IllegalStateException if trying to re-initialise existing pool
   */
  @Override
  public void initDriverPool(Supplier<Driver> driverSupplier) {
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
   * @param driverSupplier the {@link Supplier} that creates {@link Driver}s
   * @throws java.util.NoSuchElementException if this pool is empty
   */
  @Override
  public void initBrowserBeforeTest(Supplier<Driver> driverSupplier) {
    threadLocalDriver.set(driverPool.removeFirst());
  }

  @Override
  public WebDriver getWebDriver() {
    return threadLocalDriver.get().getWebDriver().getWrappedDriver();
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
      logger.error("Failed to tear down browser after test method.");
      logger.debug("Failed to tear down browser after test method.", e);
      throw e;
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
}

