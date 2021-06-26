package com.frameworkium.lite.ui;

import com.frameworkium.lite.common.properties.Property;
import com.frameworkium.lite.ui.browsers.UserAgent;
import com.frameworkium.lite.ui.capture.ScreenshotCapture;
import com.frameworkium.lite.ui.driver.DriverSetup;
import com.frameworkium.lite.ui.driver.lifecycle.*;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Handles all UI test related state and life cycle.
 * Contains a ThreadLocal instance of itself.
 */
public class UITestLifecycle {

    private static final Duration DEFAULT_TIMEOUT = Duration.of(10, SECONDS);

    private static final ThreadLocal<UITestLifecycle> THREAD_LOCAL_INSTANCE =
            ThreadLocal.withInitial(UITestLifecycle::new);

    private static DriverLifecycle driverLifecycle;
    private static String userAgent;

    private ScreenshotCapture capture;
    private Wait<WebDriver> wait;

    /** @return a ThreadLocal instance of {@link UITestLifecycle} */
    public static UITestLifecycle get() {
        return THREAD_LOCAL_INSTANCE.get();
    }

    /** @return if class initialised correctly. */
    public boolean isInitialised() {
        return wait != null;
    }

    /** Run this before the test suite to initialise a pool of drivers. */
    public static void beforeSuite() {
        if (Property.REUSE_BROWSER.getBoolean()) {
            driverLifecycle =
                    new MultiUseDriverLifecycle(
                            Property.THREADS.getIntWithDefault(1));
        } else {
            driverLifecycle = new SingleUseDriverLifecycle();
        }
        driverLifecycle.initDriverPool(DriverSetup::instantiateDriver);
    }

    /**
     * @param testMethod the method about to run, used to extract the test name
     * @see #beforeTestMethod(String)
     */
    public void beforeTestMethod(Method testMethod) {
        beforeTestMethod(getTestNameForCapture(testMethod));
    }

    /**
     * Run this before each test method to initialise:
     * the browser, wait, capture, and user agent.
     *
     * <p>This is public for times when the testMethod does not contain the
     * required test name e.g. using data providers for BDD.
     *
     * @param testName the test name for Capture
     */
    public void beforeTestMethod(String testName) {
        try {
            driverLifecycle.initBrowserBeforeTest(DriverSetup::instantiateDriver);
        } catch (WebDriverException wdex) {
            reinitialiseCurrentDriver();
        }

        wait = newWaitWithTimeout(DEFAULT_TIMEOUT);

        // Capture reads the userAgent, so ensure this is set beforehand
        updateUserAgent();

        if (ScreenshotCapture.isRequired()) {
            capture = new ScreenshotCapture(testName);
        }
    }

    private static void updateUserAgent() {
        if (userAgent == null) {
            userAgent = UserAgent.getUserAgent((JavascriptExecutor) get().getWebDriver());
        }
    }

    private String getTestNameForCapture(Method testMethod) {
        String methodName = testMethod.getName().replace("_", " ");
        return StringUtils.abbreviate(methodName, 72);
    }

    /** Run after each test method to clear or tear down the browser */
    public void afterTestMethod() {
        driverLifecycle.tearDownDriver();
    }

    /**
     * Run after the entire test suite to:
     * clear down the browser pool and send remaining screenshots to Capture.
     */
    public void afterTestSuite() {
        driverLifecycle.tearDownDriverPool();
        ScreenshotCapture.processRemainingBacklog();
        THREAD_LOCAL_INSTANCE.remove();
    }

    /**
     * Re-initialises the browser for the current thread.
     * This can be useful if the browser crashes or becomes
     * unreachable and you don't want to restart the whole suite.
     */
    public void reinitialiseCurrentDriver() {
        driverLifecycle.reinitialiseCurrentDriver(DriverSetup::instantiateDriver);
    }

    /**
     * @param timeout timeout for the new Wait
     * @return a Wait with the given timeout
     */
    public Wait<WebDriver> newWaitWithTimeout(Duration timeout) {
        return new FluentWait<>(getWebDriver())
                .withTimeout(timeout)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    public WebDriver getWebDriver() {
        return driverLifecycle.getWebDriver();
    }

    public ScreenshotCapture getCapture() {
        return capture;
    }

    public Wait<WebDriver> getWait() {
        return wait;
    }

    /**
     * @return the user agent of the browser in the first UI test to run.
     */
    public Optional<String> getUserAgent() {
        return Optional.ofNullable(userAgent);
    }

    /** @return the session ID of the remote WebDriver */
    public String getRemoteSessionId() {
        return Objects.toString(((RemoteWebDriver) getWebDriver()).getSessionId());
    }
}
