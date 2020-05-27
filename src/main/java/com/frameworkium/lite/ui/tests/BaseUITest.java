package com.frameworkium.lite.ui.tests;

import com.frameworkium.lite.common.listeners.*;
import com.frameworkium.lite.ui.UITestLifecycle;
import com.frameworkium.lite.ui.capture.ScreenshotCapture;
import com.frameworkium.lite.ui.driver.Driver;
import com.frameworkium.lite.ui.listeners.CaptureListener;
import com.frameworkium.lite.ui.listeners.ScreenshotListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.*;

import java.lang.reflect.Method;

@Listeners({CaptureListener.class, ScreenshotListener.class, TestListener.class})
@Test(groups = "base-ui")
public abstract class BaseUITest {

    /** Logger for subclasses (logs with correct class i.e. not BaseUITest). */
    protected final Logger logger = LogManager.getLogger(this);

    /**
     * Runs before the test suite to initialise a pool of drivers, if requested.
     */
    @BeforeSuite(alwaysRun = true)
    protected static void initialiseDriverPool() {
        UITestLifecycle.get().beforeSuite();
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
     * </ul>
     */
    @AfterSuite(alwaysRun = true)
    protected static void afterTestSuiteCleanUp() {
        UITestLifecycle.get().afterTestSuite();
    }

}
