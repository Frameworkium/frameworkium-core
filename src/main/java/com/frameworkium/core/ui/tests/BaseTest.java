package com.frameworkium.core.ui.tests;

import com.frameworkium.core.common.listeners.*;
import com.frameworkium.core.common.reporting.allure.AllureLogger;
import com.frameworkium.core.common.reporting.allure.AllureProperties;
import com.frameworkium.core.ui.capture.ScreenshotCapture;
import com.frameworkium.core.ui.driver.*;
import com.frameworkium.core.ui.listeners.*;
import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.testng.SauceOnDemandAuthenticationProvider;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.IMethodInstance;
import org.testng.annotations.*;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.TestCaseId;

import java.lang.reflect.Method;
import java.util.*;
import java.util.Optional;
import java.util.concurrent.*;

import static java.util.Objects.isNull;

@Listeners({CaptureListener.class, ScreenshotListener.class,
        MethodInterceptor.class, SauceLabsListener.class,
        TestListener.class, ResultLoggerListener.class})
public abstract class BaseTest
        implements SauceOnDemandSessionIdProvider, SauceOnDemandAuthenticationProvider {

    public static final ExecutorService executor = Executors.newSingleThreadExecutor();
    protected static final Logger logger = LogManager.getLogger();
    private static final long DEFAULT_TIMEOUT_SECONDS = 10L;
    private static String userAgent;
    private static ThreadLocal<Boolean> requiresReset;
    private static ThreadLocal<ScreenshotCapture> capture;
    private static ThreadLocal<Driver> driver;
    private static ThreadLocal<Wait<WebDriver>> wait;
    private static List<Driver> activeDrivers =
            Collections.synchronizedList(new ArrayList<>());

    /**
     * Method which runs first upon running a test, it will do the following:
     * <ul>
     * <li>Retrieve the {@link Driver} and initialise the {@link WebDriver}</li>
     * <li>Initialise the {@link Wait}</li>
     * <li>Initialise whether the browser needs resetting</li>
     * <li>Initialise the {@link ScreenshotCapture}</li>
     * </ul>
     */
    @BeforeSuite(alwaysRun = true)
    public static void instantiateDriverObject() {
        driver = ThreadLocal.withInitial(() -> {
            Driver newDriver =
                    new DriverSetup().returnDesiredDriverType();
            newDriver.initialise();
            activeDrivers.add(newDriver);
            return newDriver;
        });
        wait = ThreadLocal.withInitial(BaseTest::newDefaultWait);
        requiresReset = ThreadLocal.withInitial(() -> Boolean.FALSE);
        capture = ThreadLocal.withInitial(() -> null);
    }

    /**
     * The methods which configure the browser once a test runs
     * <ul>
     * <li>Maximises browser based on the driver type</li>
     * <li>Initialises screenshot capture if needed</li>
     * <li>Resets the browser if another test ran prior</li>
     * <li>Sets the user agent of the browser</li>
     * </ul>
     *
     * @param testMethod The test method name of the test
     */
    @BeforeMethod(alwaysRun = true)
    public static void configureBrowserBeforeTest(Method testMethod) {
        try {
            configureDriverBasedOnParams();
            initialiseNewScreenshotCapture(testMethod);
        } catch (Exception e) {
            logger.error("Failed to configure browser.", e);
            throw new RuntimeException("Failed to configure browser.", e);
        }
    }

    /** TODO: Should be refactored */
    private static void configureDriverBasedOnParams() {
        if (requiresReset.get()) {
            driver.get().resetBrowser();
            wait.set(newDefaultWait());
        } else {
            requiresReset.set(true);
        }
        userAgent = determineUserAgent();
    }

    private static String determineUserAgent() {
        try {
            return (String) getDriver().executeScript("return navigator.userAgent;");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Initialise the screenshot capture and link to issue/test case id
     *
     * @param testMethod Test method passed from the test script
     */
    private static void initialiseNewScreenshotCapture(Method testMethod) {
        if (ScreenshotCapture.isRequired()) {
            Optional<String> testID = getIssueOrTestCaseIdValue(testMethod);
            if (testID.orElse("").isEmpty()) {
                logger.warn("{} doesn't have a TestID annotation.", testMethod.getName());
                testID = Optional.of(StringUtils.abbreviate(testMethod.getName(), 20));
            }
            capture.set(new ScreenshotCapture(testID.orElse("n/a")));
        }
    }

    /**
     * TODO: doesn't belong in this class
     * @param method the method to check for test ID annotations.
     * @return Optional of the {@link TestCaseId} or {@link Issue} value.
     * @throws IllegalStateException if {@link TestCaseId} and {@link Issue}
     *                               are both specified inconstantly.
     */
    public static Optional<String> getIssueOrTestCaseIdValue(Method method) {
        TestCaseId tcIdAnnotation = method.getAnnotation(TestCaseId.class);
        Issue issueAnnotation = method.getAnnotation(Issue.class);

        if (!isNull(issueAnnotation) && !isNull(tcIdAnnotation)
                && !issueAnnotation.value().equals(tcIdAnnotation.value())) {
            throw new IllegalStateException(
                    "TestCaseId and Issue annotation are both specified but " +
                            "not equal for method: " + method.toString());
        }

        if (!isNull(issueAnnotation)) {
            return Optional.of(issueAnnotation.value());
        } else if (!isNull(tcIdAnnotation)) {
            return Optional.of(tcIdAnnotation.value());
        } else {
            return Optional.empty();
        }
    }

    /**
     * @return a new {@link Wait} for the thread local driver and default timeout
     */
    public static Wait<WebDriver> newDefaultWait() {
        return newWaitWithTimeout(DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * @param timeout timeout in seconds for the {@link Wait}
     * @return a new {@link Wait} for the thread local driver and given timeout
     */
    public static Wait<WebDriver> newWaitWithTimeout(long timeout) {
        return new FluentWait<>(getDriver().getWrappedDriver())
                .withTimeout(timeout, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    /**
     * @return the {@link WebDriverWrapper} instance for the requesting thread
     */
    public static WebDriverWrapper getDriver() {
        return driver.get().getDriver();
    }

    /**
     * @param iMethod the {@link IMethodInstance} to check for test ID annotations.
     * @return Optional of either the {@link TestCaseId} or {@link Issue} value.
     * @throws IllegalStateException if {@link TestCaseId} and {@link Issue}
     *                               are specified inconstantly.
     */
    public static Optional<String> getIssueOrTestCaseIdValue(IMethodInstance iMethod) {
        Method method = iMethod.getMethod().getConstructorOrMethod().getMethod();
        return getIssueOrTestCaseIdValue(method);
    }

    /** Loops through all active driver types and tears them down */
    @AfterSuite(alwaysRun = true)
    public static void closeDriverObject() {
        try {
            activeDrivers.stream().parallel()
                    .forEach(Driver::tearDown);
        } catch (Exception e) {
            logger.warn("Session quit unexpectedly.", e);
        }
    }

    /** Shuts down the {@link ExecutorService} */
    @AfterSuite(alwaysRun = true)
    public static void shutdownExecutor() {
        try {
            executor.shutdown();
            executor.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("Executor was interrupted while shutting down. " +
                    "Some tasks might not have been executed.");
        }
    }

    /** Creates the allure properties for the report */
    @AfterSuite(alwaysRun = true)
    public static void createAllureProperties() {
        AllureProperties.create();
    }

    /** @return the {@link ScreenshotCapture} object for the current test */
    public static ScreenshotCapture getCapture() {
        return capture.get();
    }

    /** @return The default {@link Wait} */
    public static Wait<WebDriver> getWait() {
        return wait.get();
    }

    /** @return Optional of the current browser user agent */
    public static Optional<String> getUserAgent() {
        return Optional.ofNullable(userAgent);
    }

    /** @return the Job id for the current thread */
    @Override
    public String getSessionId() {
        SessionId sessionId = getDriver().getWrappedRemoteWebDriver().getSessionId();
        return isNull(sessionId) ? null : sessionId.toString();
    }

    /**
     * @return the {@link SauceOnDemandAuthentication} instance containing
     * the Sauce username/access key
     */
    @Override
    public SauceOnDemandAuthentication getAuthentication() {
        return new SauceOnDemandAuthentication();
    }

    /**
     * Logs the start of a step to your allure report
     * Other steps will be sub-steps until you call stepFinish
     *
     * @param stepName the name of the step
     * @deprecated use <code>AllureLogger.__stepStart(stepName)</code>
     */
    @Deprecated
    public void __stepStart(String stepName) {
        AllureLogger.__stepStart(stepName);
    }

    /**
     * Logs the end of a step
     *
     * @deprecated use <code>AllureLogger.__stepFinish()</code>
     */
    @Deprecated
    public void __stepFinish() {
        AllureLogger.__stepFinish();
    }
}
