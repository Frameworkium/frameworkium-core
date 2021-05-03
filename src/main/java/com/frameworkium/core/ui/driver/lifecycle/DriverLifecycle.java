package com.frameworkium.core.ui.driver.lifecycle;

import com.frameworkium.core.ui.driver.Driver;
import java.util.function.Supplier;
import org.openqa.selenium.WebDriver;

/**
 * Controls the lifecycle of the {@link Driver}(s).
 *
 * <p>The methods need to be called in order:
 * <ol>
 * <li>{@link #initDriverPool(Supplier)}
 * (once until {@link #tearDownDriverPool()} has been called)
 * <li>{@link #initBrowserBeforeTest(Supplier)}
 * (once until {@link #tearDownDriver()} has been called)
 * <li>{@link #getWebDriver()} (n times only after the above and before the below)
 * <li>{@link #tearDownDriver()}
 * (once after {@link #initBrowserBeforeTest(Supplier)} has been called)
 * <li>{@link #tearDownDriverPool()} (once but multiple calls do nothing)
 * </ol>
 */
public interface DriverLifecycle {

  /**
   * Will initialise a pool of {@link Driver}s if required.
   *
   * @param driverSupplier the {@link Supplier} that creates {@link Driver}s
   * @throws IllegalStateException if trying to re-initialise existing pool
   */
  default void initDriverPool(Supplier<Driver> driverSupplier) {
  }

  /**
   * Will set the current {@link ThreadLocal} {@link Driver} to be the next
   * available from the pool or will add the {@link Driver} created by the
   * supplied {@link Supplier}.
   *
   * @param driverSupplier the {@link Supplier} that creates {@link Driver}s
   * @throws java.util.NoSuchElementException if this pool is empty
   */
  void initBrowserBeforeTest(Supplier<Driver> driverSupplier);

  /**
   * @return the {@link WebDriver} in use by the current thread.
   * @throws NullPointerException if called before
   *                              {@link #initBrowserBeforeTest(Supplier)} or
   *                              after {@link #tearDownDriver()}.
   */
  WebDriver getWebDriver();

  /**
   * Tears down the driver, ready for reinitialisation, if required, by
   * {@link #initBrowserBeforeTest(Supplier)}.
   */
  void tearDownDriver();

  /**
   * Clears the driver pool, if exists, ready to run run
   * {@link #initDriverPool(Supplier)} again if required.
   */
  default void tearDownDriverPool() {
  }
}
