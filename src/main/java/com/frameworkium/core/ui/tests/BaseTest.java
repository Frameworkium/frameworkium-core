package com.frameworkium.core.ui.tests;

import com.frameworkium.core.common.listeners.*;
import com.frameworkium.core.common.reporting.TestIdUtils;
import com.frameworkium.core.common.reporting.allure.AllureLogger;
import com.frameworkium.core.common.reporting.allure.AllureProperties;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.isNull;
import static java.util.concurrent.TimeUnit.SECONDS;

@Listeners({CaptureListener.class, ScreenshotListener.class, MethodInterceptor.class, SauceLabsListener.class,
        TestListener.class, ResultLoggerListener.class, VideoListener.class})
public abstract class BaseTest implements SauceOnDemandSessionIdProvider, SauceOnDemandAuthenticationProvider {

    /** Executor for async sending of screenshot messages to capture. */
    public static final ExecutorService screenshotExecutor =
            Executors.newSingleThreadExecutor();

    /** Driver related constant. */
    public static final long DEFAULT_TIMEOUT_SECONDS = 10L;

    /** Logger for this class. */
    private static final Logger baseLogger = LogManager.getLogger();

    private static final ThreadLocal<ScreenshotCapture> capture = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<Driver> driver = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<Wait<WebDriver>> wait = ThreadLocal.withInitial(() -> null);
    private static String userAgent;

    /** Logger for subclasses (logs with correct class i.e. not BaseTest). */
    protected final Logger logger = LogManager.getLogger(this);

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
    public static void instantiateDriverObject() {
        driver.set(new DriverSetup().instantiateDriver());
        wait.set(newDefaultWait());
    }

    /**
     * Configure browser before test.
     *
     * @param testMethod The test method about to be executed
     * @see #configureBrowserBeforeTest(String)
     */
    @BeforeMethod(alwaysRun = true, dependsOnMethods = "instantiateDriverObject")
    public static void configureBrowserBeforeTest(Method testMethod) {
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
    public static void configureBrowserBeforeTest(String testName) {
        try {
            wait.set(newDefaultWait());
            userAgent = determineUserAgent();
            if (ScreenshotCapture.isRequired()) {
                initialiseNewScreenshotCapture(testName);
            }
        } catch (Exception e) {
            baseLogger.error("Failed to configure browser.", e);
            throw new RuntimeException("Failed to configure browser.", e);
        }
    }

    /** Tears down the browser after the test method. */
    @AfterMethod(alwaysRun = true)
    public static void tearDownBrowser() {
        try {
            driver.get().tearDown();
        } catch (Exception e) {
            baseLogger.warn("Session quit unexpectedly.", e);
        }
    }

    /** Shuts down the {@link ExecutorService}. */
    @AfterSuite(alwaysRun = true)
    public static void shutdownScreenshotExecutor() {
        baseLogger.debug("Async screenshot capture: processing remaining backlog...");
        screenshotExecutor.shutdown();
        try {
            boolean timeout = !screenshotExecutor.awaitTermination(60, SECONDS);
            if (timeout) {
                baseLogger.error("Async screenshot capture: shutdown timed out. "
                        + "Some screenshots might not have been sent.");
            } else {
                baseLogger.debug("Async screenshot capture: finished backlog.");
            }
        } catch (InterruptedException e) {
            baseLogger.error("Async screenshot capture: executor was interrupted. "
                    + "Some screenshots might not have been sent.");
        }
    }

    /** Creates the allure properties for the report. */
    @AfterSuite(alwaysRun = true)
    public static void createAllureProperties() {
        AllureProperties.create();
    }

    /** Required for unit testing. */
    public static void setDriver(Driver newDriver) {
        driver.set(newDriver);
    }

    /** Required for unit testing. */
    public static void setWait(Wait<WebDriver> newWait) {
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

    private static String determineUserAgent() {
        try {
            return Driver.isNative() ? "" : (String) getDriver().executeScript("return navigator.userAgent;");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Initialise the screenshot capture and link to issue/test case id.
     *
     * @param testName Test method passed from the test script
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
     * @return a new {@link Wait} for the thread local driver and given timeout which also ignores {@link
     *         NoSuchElementException} and {@link StaleElementReferenceException}
     */
    public static Wait<WebDriver> newWaitWithTimeout(long timeout) {
        return new FluentWait<>(getDriver().getWrappedDriver())
                .withTimeout(timeout, SECONDS)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    /**
     * Get {@link WebDriverWrapper} instance for the requesting thread.
     *
     * @return the {@link WebDriverWrapper}
     */
    public static WebDriverWrapper getDriver() {
        return driver.get().getDriver();
    }

    /**
     * Get {@link ScreenshotCapture} object for the current test.
     *
     * @return the {@link ScreenshotCapture} object
     */
    public static ScreenshotCapture getCapture() {
        return capture.get();
    }

    /** Get the default {@link Wait}. */
    public static Wait<WebDriver> getWait() {
        return wait.get();
    }

    /**
     * Get current browser user agent.
     *
     * @return Optional of the current browser user agent
     */
    public static Optional<String> getUserAgent() {
        return Optional.ofNullable(userAgent);
    }

    /**
     * Get session id for the current thread.
     *
     * @return Session id
     */
    public static String getThreadSessionId() {
        SessionId sessionId = getDriver().getWrappedRemoteWebDriver().getSessionId();
        return isNull(sessionId) ? null : sessionId.toString();
    }

    /**
     * Get the Job id for the current thread.
     *
     * @return Job id
     */
    @Override
    public String getSessionId() {
        return getThreadSessionId();
    }

    /**
     * Get {@link SauceOnDemandAuthentication} instance containing the Sauce username/access key.
     *
     * @return {@link SauceOnDemandAuthentication} instance
     */
    @Override
    public SauceOnDemandAuthentication getAuthentication() {
        return new SauceOnDemandAuthentication();
    }

    /**
     * Logs the start of a step to your allure report
     * Other steps will be sub-steps until you call stepFinish.
     *
     * @param stepName the name of the step
     * @deprecated use <code>AllureLogger.stepStart(stepName)</code>
     */
    @Deprecated
    public void __stepStart(String stepName) {
        AllureLogger.stepStart(stepName);
    }

    /**
     * Logs the end of a step.
     *
     * @deprecated use <code>AllureLogger.stepFinish()</code>
     */
    @Deprecated
    public void __stepFinish() {
        AllureLogger.stepFinish();
    }
}
