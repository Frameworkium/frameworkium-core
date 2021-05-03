package com.frameworkium.core.ui.tests;

import com.frameworkium.core.common.listeners.MethodInterceptor;
import com.frameworkium.core.common.listeners.ResultLoggerListener;
import com.frameworkium.core.common.listeners.TestListener;
import com.frameworkium.core.ui.UITestLifecycle;
import com.frameworkium.core.ui.capture.ScreenshotCapture;
import com.frameworkium.core.ui.driver.Driver;
import com.frameworkium.core.ui.listeners.CaptureListener;
import com.frameworkium.core.ui.listeners.SauceLabsListener;
import com.frameworkium.core.ui.listeners.ScreenshotListener;
import com.frameworkium.core.ui.listeners.VideoListener;
import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.testng.SauceOnDemandAuthenticationProvider;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({
    CaptureListener.class, ScreenshotListener.class, MethodInterceptor.class,
    SauceLabsListener.class, TestListener.class, ResultLoggerListener.class,
    VideoListener.class})
@Test(groups = "base-ui")
public abstract class BaseUITest
    implements SauceOnDemandSessionIdProvider, SauceOnDemandAuthenticationProvider {

  /**
   * Logger for subclasses (logs with correct class i.e. not BaseUITest).
   */
  protected final Logger logger = LogManager.getLogger(this);

  /**
   * Runs before the test suite to initialise a pool of drivers, if requested.
   */
  @BeforeSuite(alwaysRun = true)
  protected static void initialiseDriverPool() {
    UITestLifecycle.get().beforeSuite();
  }

  /**
   * Tears down the browser after the test method.
   */
  @AfterMethod(alwaysRun = true)
  protected static void tearDownDriver() {
    UITestLifecycle.get().afterTestMethod();
  }

  /**
   * <ul>
   * <li>Ensures each driver in the pool has {@code quit()}
   * <li>Processes remaining screenshot backlog
   * <li>Create Allure properties
   * </ul>
   */
  @AfterSuite(alwaysRun = true)
  protected static void afterTestSuiteCleanUp() {
    UITestLifecycle.get().afterTestSuite();
  }

  /**
   * Create a new {@link Wait} with ThreadLocal driver and default timeout.
   *
   * @deprecated use {@code UITestLifecycle.get().getWait()}.
   */
  @Deprecated
  public static Wait<WebDriver> newDefaultWait() {
    return UITestLifecycle.get().newDefaultWait();
  }

  /**
   * Create a new {@link Wait} with timeout.
   *
   * @param timeout timeout {@link Duration} for the {@link Wait}
   * @return a new {@link Wait} for the thread local driver and given timeout
   *     which also ignores {@link NoSuchElementException} and
   *     {@link StaleElementReferenceException}
   * @deprecated use {@code UITestLifecycle.get().newWaitWithTimeout(Duration)}.
   */
  @Deprecated
  public static Wait<WebDriver> newWaitWithTimeout(Duration timeout) {
    return UITestLifecycle.get().newWaitWithTimeout(timeout);
  }

  /**
   * @return the {@link WebDriver} used by the current thread.
   * @deprecated use {@code UITestLifecycle.get().getWebDriver()}.
   */
  @Deprecated
  public static WebDriver getWebDriver() {
    return UITestLifecycle.get().getWebDriver();
  }

  /**
   * @deprecated use {@code UITestLifecycle.get().getCapture()}.
   */
  @Deprecated
  public static ScreenshotCapture getCapture() {
    return UITestLifecycle.get().getCapture();
  }

  /**
   * @deprecated use {@code UITestLifecycle.get().getWait()}.
   */
  @Deprecated
  public static Wait<WebDriver> getWait() {
    return UITestLifecycle.get().getWait();
  }

  /**
   * @return the user agent of the browser in the first UI test to run.
   * @deprecated use {@code UITestLifecycle.get().getUserAgent()}.
   */
  @Deprecated
  public static Optional<String> getUserAgent() {
    return UITestLifecycle.get().getUserAgent();
  }

  /**
   * @deprecated use {@code UITestLifecycle.get().getRemoteSessionId()}.
   */
  @Deprecated
  public static String getThreadSessionId() {
    return UITestLifecycle.get().getRemoteSessionId();
  }

  /**
   * Runs before each test method, it initialises the following:
   * <ul>
   * <li>{@link Driver} and {@link WebDriver}</li>
   * <li>{@link Wait}</li>
   * <li>{@link ScreenshotCapture}</li>
   * <li>userAgent</li>
   * </ul>
   */
  @BeforeMethod(alwaysRun = true)
  protected void configureBrowserBeforeTest(Method testMethod) {
    UITestLifecycle.get().beforeTestMethod(testMethod);
  }

  @Override
  public String getSessionId() {
    return UITestLifecycle.get().getRemoteSessionId();
  }

  @Override
  public SauceOnDemandAuthentication getAuthentication() {
    return new SauceOnDemandAuthentication();
  }
}
