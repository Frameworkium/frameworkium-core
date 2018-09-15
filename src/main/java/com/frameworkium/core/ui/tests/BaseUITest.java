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
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;

@Listeners({CaptureListener.class, ScreenshotListener.class, MethodInterceptor.class,
        SauceLabsListener.class, TestListener.class, ResultLoggerListener.class,
        VideoListener.class})
@Test(groups = "base-ui")
public abstract class BaseUITest
        implements SauceOnDemandSessionIdProvider, SauceOnDemandAuthenticationProvider {

    private static final Duration DEFAULT_TIMEOUT = Duration.of(10, SECONDS);

    /** Logger for subclasses (logs with correct class i.e. not BaseUITest). */
    protected final Logger logger = LogManager.getLogger(this);

    private static final Logger baseLogger = LogManager.getLogger();

    private static final ThreadLocal<ScreenshotCapture> capture = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<Wait<WebDriver>> wait = ThreadLocal.withInitial(() -> null);

    private static DriverLifecycle driverLifecycle;
    private static String userAgent;

    /**
     * Runs before the test suite to initialise a pool of drivers, if requested.
     */
    @BeforeSuite(alwaysRun = true)
    protected static void initialiseDriverPool() {
        driverLifecycle = new DriverLifecycle(
                Property.THREADS.getIntWithDefault(1),
                Property.REUSE_BROWSER.getBoolean());
        driverLifecycle.initDriverPool(DriverSetup::instantiateDriver);
    }

    /**
     * Method which runs first upon running a test, it will do the following.
     * <ul>
     * <li>Retrieve the {@link Driver} and initialise the {@link WebDriver}</li>
     * <li>Initialise the {@link Wait}</li>
     * <li>Initialise the {@link ScreenshotCapture}</li>
     * </ul>
     */
    @BeforeMethod(alwaysRun = true)
    protected static void configureBrowserBeforeTest(Method testMethod) {
        driverLifecycle.initBrowserBeforeTest(DriverSetup::instantiateDriver);

        wait.set(newDefaultWait());

        if (ScreenshotCapture.isRequired()) {
            String testName = getTestNameForCapture(testMethod);
            capture.set(new ScreenshotCapture(testName));
        }

        if (userAgent == null) {
            userAgent = UserAgent.getUserAgent((JavascriptExecutor) getWebDriver());
        }
    }

    /** Tears down the browser after the test method. */
    @AfterMethod(alwaysRun = true)
    protected static void tearDownDriver() {
        driverLifecycle.tearDownDriver();
    }

    @AfterSuite(alwaysRun = true)
    protected static void tearDownSuite() {
        driverLifecycle.tearDownDriverPool();
    }

    @AfterSuite(alwaysRun = true)
    protected static void shutdownScreenshotExecutor() {
        if (!ScreenshotCapture.isRequired()) {
            return;
        }
        String prefix = "Screenshot Capture: ";
        baseLogger.info(prefix + "processing remaining async backlog...");
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
        AllureProperties.createUI();
    }

    /**
     * Find the calling method and pass it into
     * {@link #configureBrowserBeforeTest(Method)} to configure the browser.
     *
     * @deprecated use the following test annotation instead
     *         {@code @BeforeMethod(dependsOnMethods = "configureBrowserBeforeTest")}
     */
    @Deprecated
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
        Optional<String> testID = TestIdUtils.getIssueOrTmsLinkValue(testMethod);
        if (!testID.isPresent() || testID.get().isEmpty()) {
            testID = Optional.of(StringUtils.abbreviate(testMethod.getName(), 20));
        }
        return testID.orElse("n/a");
    }

    /** Create a new {@link Wait} for the thread local driver and default timeout. */
    public static Wait<WebDriver> newDefaultWait() {
        return newWaitWithTimeout(DEFAULT_TIMEOUT);
    }

    /**
     * Create a new {@link Wait} with timeout.
     *
     * @param timeout timeout {@link Duration} for the {@link Wait}
     * @return a new {@link Wait} for the thread local driver and given timeout
     *         which also ignores {@link NoSuchElementException} and
     *         {@link StaleElementReferenceException}
     */
    public static Wait<WebDriver> newWaitWithTimeout(Duration timeout) {
        return new FluentWait<>(getWebDriver())
                .withTimeout(timeout)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    /**
     * @return the {@link WebDriver} used by the current thread.
     * @deprecated use {@link #getWebDriver()} instead because this name
     *         conflicts with {@link Driver}
     */
    @Deprecated
    public static WebDriver getDriver() {
        return driverLifecycle.getWebDriver();
    }

    /**
     * @return the {@link WebDriver} used by the current thread.
     */
    public static WebDriver getWebDriver() {
        return driverLifecycle.getWebDriver();
    }

    public static ScreenshotCapture getCapture() {
        return capture.get();
    }

    public static Wait<WebDriver> getWait() {
        return wait.get();
    }

    /**
     * @return the user agent of the browser in the first UI test to run.
     */
    public static Optional<String> getUserAgent() {
        return Optional.ofNullable(userAgent);
    }

    public static String getThreadSessionId() {
        return Objects.toString(((RemoteWebDriver) getWebDriver()).getSessionId());
    }

    @Override
    public String getSessionId() {
        return getThreadSessionId();
    }

    @Override
    public SauceOnDemandAuthentication getAuthentication() {
        return new SauceOnDemandAuthentication();
    }
}
