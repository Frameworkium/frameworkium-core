package com.frameworkium.core.ui.tests;

import com.frameworkium.core.common.listeners.*;
import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.common.reporting.TestIdUtils;
import com.frameworkium.core.common.reporting.allure.AllureProperties;
import com.frameworkium.core.ui.browsers.UserAgent;
import com.frameworkium.core.ui.capture.ScreenshotCapture;
import com.frameworkium.core.ui.driver.*;
import com.frameworkium.core.ui.listeners.*;
import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.testng.SauceOnDemandAuthenticationProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.concurrent.TimeUnit.SECONDS;

@Listeners({CaptureListener.class, ScreenshotListener.class, MethodInterceptor.class,
        SauceLabsListener.class, TestListener.class, ResultLoggerListener.class,
        VideoListener.class})
public abstract class BaseUITest implements SauceOnDemandSessionIdProvider, SauceOnDemandAuthenticationProvider {

    public static final long DEFAULT_TIMEOUT_SECONDS = 10L;

    /** Logger for subclasses (logs with correct class i.e. not BaseUITest). */
    protected final Logger logger = LogManager.getLogger(this);

    private static final Logger baseLogger = LogManager.getLogger();

    private static final ThreadLocal<ScreenshotCapture> capture = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<Driver> driver = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<Wait<WebDriver>> wait = ThreadLocal.withInitial(() -> null);
    private static UserAgent userAgent;

    private static BlockingQueue<Driver> driverPool;

    /**
     * Runs before the test suite to initialise a pool of drivers if requested.
     */
    @BeforeSuite
    protected static void initialiseDriverPool() {
        if (Property.REUSE_BROWSER.getBoolean()) {
            int threads = Property.THREADS.getIntWithDefault(1);
            driverPool = new ArrayBlockingQueue<>(threads);
            IntStream.range(0, threads)
                    .mapToObj(i -> new DriverSetup().instantiateDriver())
                    .forEach(driverPool::add);
        }
    }

    /**
     * Method which runs first upon running a test, it will do the following.
     * <ul>
     * <li>Retrieve the {@link Driver} and initialise the {@link WebDriver}</li>
     * <li>Initialise the {@link Wait}</li>
     * <li>Initialise whether the browser needs resetting</li>
     * <li>Initialise the {@link ScreenshotCapture}</li>
     * </ul>
     */
    @BeforeMethod(alwaysRun = true)
    protected static void instantiateDriverObject() {
        if (Property.REUSE_BROWSER.getBoolean()) {
            driver.set(getNextAvailableDriverFromPool());
        } else {
            driver.set(new DriverSetup().instantiateDriver());
        }
        wait.set(newDefaultWait());
    }

    /**
     * The pool should not be empty here. It has been initialised with one driver
     * per thread and each driver is returned to the pool upon test completion.
     */
    private static Driver getNextAvailableDriverFromPool() {
        return driverPool.remove();
    }

    /**
     * @param testMethod The test method about to be executed
     * @see #configureBrowserBeforeTest(String)
     */
    @BeforeMethod(alwaysRun = true, dependsOnMethods = "instantiateDriverObject")
    protected static void configureBrowserBeforeTest(Method testMethod) {
        configureBrowserBeforeTest(getTestNameForCapture(testMethod));
    }

    /**
     * Configure the browser before a test method runs.
     * <ul>
     * <li>Resets the browser if already initialised</li>
     * <li>Maximises browser based on settings</li>
     * <li>Sets the user agent of the browser</li>
     * <li>Initialises screenshot capture if needed</li>
     * </ul>
     *
     * @param testName The test name about to be executed
     */
    private static void configureBrowserBeforeTest(String testName) {
        try {
            wait.set(newDefaultWait());
            userAgent = new UserAgent(getDriver());
            if (ScreenshotCapture.isRequired()) {
                initialiseNewScreenshotCapture(testName);
            }
        } catch (Exception e) {
            baseLogger.error("Failed to configure browser.", e);
            throw new IllegalStateException("Failed to configure browser.", e);
        }
    }

    /** Tears down the browser after the test method. */
    @AfterMethod(alwaysRun = true)
    protected static void tearDownBrowser() {
        try {
            if (Property.REUSE_BROWSER.getBoolean()) {
                driverPool.add(driver.get());
            } else {
                driver.get().tearDown();
            }
        } catch (Exception e) {
            baseLogger.warn("Session quit unexpectedly.", e);
        }
    }

    /** Shuts down the {@link ExecutorService}. */
    @AfterSuite(alwaysRun = true)
    protected static void tearDownSuite() {
        if (Property.REUSE_BROWSER.getBoolean()) {
            driverPool.forEach(d -> d.getDriver().quit());
        }
    }

    @AfterSuite(alwaysRun = true)
    protected static void shutdownScreenshotExecutor() {
        if (!ScreenshotCapture.isRequired()) {
            return;
        }
        String prefix = "Async screenshot capture: ";
        baseLogger.info(prefix + "processing remaining backlog...");
        try {
            boolean timeout = !ScreenshotCapture.processRemainingBacklog();
            if (timeout) {
                baseLogger.error(prefix + "shutdown timed out. "
                        + "Some screenshots might not have been sent.");
            } else {
                baseLogger.info(prefix + "finished backlog.");
            }
        } catch (InterruptedException e) {
            baseLogger.error(prefix + "executor was interrupted. "
                    + "Some screenshots might not have been sent.");
        }
    }

    /** Creates the allure properties for the report. */
    @AfterSuite(alwaysRun = true)
    protected static void createAllureProperties() {
        AllureProperties.create();
    }

    /** Required for unit testing. */
    static void setDriver(Driver newDriver) {
        driver.set(newDriver);
    }

    /** Required for unit testing. */
    static void setWait(Wait<WebDriver> newWait) {
        wait.set(newWait);
    }

    /**
     * Find the calling method and pass it into
     * {@link #configureBrowserBeforeTest(Method)} to configure the browser.
     */
    protected static void configureBrowserBeforeUse() {
        configureBrowserBeforeTest(
                getCallingMethod(Thread.currentThread().getStackTrace()[2]));
    }

    private static Method getCallingMethod(StackTraceElement stackTraceElement) {
        String className = stackTraceElement.getClassName();
        String methodName = stackTraceElement.getMethodName();
        try {
            return Class.forName(className).getDeclaredMethod(methodName);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getTestNameForCapture(Method testMethod) {
        Optional<String> testID = TestIdUtils.getIssueOrTestCaseIdValue(testMethod);
        if (!testID.isPresent() || testID.get().isEmpty()) {
            baseLogger.warn("{} doesn't have a TestID annotation.", testMethod.getName());
            testID = Optional.of(StringUtils.abbreviate(testMethod.getName(), 20));
        }
        return testID.orElse("n/a");
    }

    /**
     * Initialise the screenshot capture and link to issue/test case id.
     */
    private static void initialiseNewScreenshotCapture(String testName) {
        capture.set(new ScreenshotCapture(testName));
    }

    /** Create a new {@link Wait} for the thread local driver and default timeout. */
    public static Wait<WebDriver> newDefaultWait() {
        return newWaitWithTimeout(DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * Create a new {@link Wait} with timeout.
     *
     * @param timeout timeout in seconds for the {@link Wait}
     * @return a new {@link Wait} for the thread local driver and given timeout
     *         which also ignores {@link NoSuchElementException} and
     *         {@link StaleElementReferenceException}
     */
    public static Wait<WebDriver> newWaitWithTimeout(long timeout) {
        return new FluentWait<>(getDriver().getWrappedDriver())
                .withTimeout(timeout, SECONDS)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    public static WebDriverWrapper getDriver() {
        return driver.get().getDriver();
    }

    public static ScreenshotCapture getCapture() {
        return capture.get();
    }

    public static Wait<WebDriver> getWait() {
        return wait.get();
    }

    public static Optional<String> getUserAgent() {
        if (userAgent != null) {
            return userAgent.getUserAgent();
        } else {
            return Optional.empty();
        }
    }

    public static String getThreadSessionId() {
        SessionId sessionId = getDriver().getWrappedRemoteWebDriver().getSessionId();
        return isNull(sessionId) ? null : sessionId.toString();
    }

    @Override
    public String getSessionId() {
        return getThreadSessionId();
    }

    /**
     * Get {@link SauceOnDemandAuthentication} instance containing the Sauce username/access key.
     */
    @Override
    public SauceOnDemandAuthentication getAuthentication() {
        return new SauceOnDemandAuthentication();
    }
}
