package com.frameworkium.tests.internal;

import java.lang.reflect.Method;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.frameworkium.config.DriverType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.remote.SessionNotFoundException;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.TestCaseId;

import com.frameworkium.capture.ScreenshotCapture;
import com.frameworkium.config.WebDriverWrapper;
import com.frameworkium.listeners.CaptureListener;
import com.frameworkium.listeners.MethodInterceptor;
import com.frameworkium.listeners.ResultLoggerListener;
import com.frameworkium.listeners.SauceLabsListener;
import com.frameworkium.listeners.ScreenshotListener;
import com.frameworkium.listeners.TestListener;
import com.frameworkium.reporting.AllureProperties;
import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.testng.SauceOnDemandAuthenticationProvider;

import static com.frameworkium.config.DriverSetup.returnDesiredDriverType;
import static com.frameworkium.config.SystemProperty.MAXIMISE;

@Listeners({CaptureListener.class, ScreenshotListener.class, MethodInterceptor.class, SauceLabsListener.class,
        TestListener.class, ResultLoggerListener.class})

public abstract class BaseTest implements SauceOnDemandSessionIdProvider, SauceOnDemandAuthenticationProvider {

    private static ThreadLocal<Boolean> requiresReset;
    private static ThreadLocal<ScreenshotCapture> capture;
    private static ThreadLocal<DriverType> driverType;
    private static List<DriverType> activeDriverTypes
            = Collections.synchronizedList(new ArrayList<DriverType>());
    private static Logger logger = LogManager.getLogger(BaseTest.class);

    public static String userAgent;

    /**
     * Method which runs first upon running a test, it will do the following:
     *  - Retrieve the desired driver type and initialise the driver
     *  - Initialise whether the browser needs resetting
     *  - Initialise the screenshot capture
     *  - Configure the browser based on paramaters (maximise window, session resets, user agent)
     */
    @BeforeSuite(alwaysRun = true)
    public static void instantiateDriverObject() {
        driverType = new ThreadLocal<DriverType>() {
            @Override
            protected DriverType initialValue() {
                DriverType driverType = returnDesiredDriverType();
                driverType.instantiate();
                activeDriverTypes.add(driverType);
                return driverType;
            }
        };
        requiresReset = new ThreadLocal<Boolean>() {
            @Override
            protected Boolean initialValue() {
                return Boolean.FALSE;
            }
        };
        capture = new ThreadLocal<ScreenshotCapture>() {
            @Override
            protected ScreenshotCapture initialValue() {
                return null;
            }
        };
        configureDriverBasedOnParams();
    }

    /**
     * Initialise the screenshot capture and link to issue/test case id
     *
     * @param testMethod - Test method passed from the test script
     */
    @BeforeMethod
    public static void initialiseNewScreenshotCapture(Method testMethod) {
        if (ScreenshotCapture.isRequired()) {
            String testID = "n/a";
            try {
                testID = testMethod.getName();
            } catch (NullPointerException e) {}
            try {
                testID = testMethod.getAnnotation(Issue.class).value();
            } catch (NullPointerException e) {
                logger.debug("No Issue defined.");
            }
            try {
                testID = testMethod.getAnnotation(TestCaseId.class).value();
            } catch (NullPointerException e) {
                logger.debug("No Test Case ID defined.");
            }

            capture.set(new ScreenshotCapture(testID, driverType.get().getDriver()));
        }
    }

    /**
     * Ran as part of the initialiseDriverObject, configures parts of the driver
     */
    private static void configureDriverBasedOnParams() {
        requiresReset.set(driverType.get().clearSession(requiresReset.get()));
        driverType.get().maximiseBrowserWindow();
        setUserAgent();
    }

    /**
     * Returns the webdriver object for that given thread
     *
     * @return - WebDriver object
     */
    public static WebDriverWrapper getDriver() {
        return driverType.get().getDriver();
    }

    /**
     * Sets the user agent of the browser for the test run
     */
    private static void setUserAgent() {
        userAgent = getUserAgent();
    }

    /**
     * Loops through all active driver types and tears down the driver object
     */
    @AfterSuite(alwaysRun = true)
    public static void closeDriverObject() {
        for (DriverType driverType : activeDriverTypes) {
            try {
                driverType.tearDownDriver();
            } catch (Exception e) {
                logger.warn("Session quit unexpectedly.", e);
            }
        }
    }

    /**
     * Creates the allure properties for the report, after the test run
     */
    @AfterSuite(alwaysRun = true)
    public static void createAllureProperties() {
        AllureProperties.create();
    }

    /**
     *  @return the Job id for the current thread
     */
    @Override
    public String getSessionId() {
        SessionId sessionId = getDriver().getWrappedRemoteWebDriver().getSessionId();
        return (sessionId == null) ? null : sessionId.toString();
    }

    /**
     * Retrieves the user agent from the browser
     * @return - String of the user agent
     */
    private static String getUserAgent() {
        String ua;
        try {
            ua = (String) getDriver().executeScript("return navigator.userAgent;");
        } catch (Exception e) {
            ua = "Unable to fetch UserAgent";
        }
        logger.debug("User agent is: '" + ua + "'");
        return ua;
    }

    /**
     * @return the {@link SauceOnDemandAuthentication} instance containing the Sauce username/access key
     */
    @Override
    public SauceOnDemandAuthentication getAuthentication() {
        return new SauceOnDemandAuthentication();
    }

    /**
     * @return - Screenshot capture object for the current test
     */
    public static ScreenshotCapture getCapture() {
        return capture.get();
    }
}
