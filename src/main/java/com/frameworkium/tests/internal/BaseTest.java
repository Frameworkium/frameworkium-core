package com.frameworkium.tests.internal;

import com.frameworkium.capture.ScreenshotCapture;
import com.frameworkium.config.DriverSetup;
import com.frameworkium.config.DriverType;
import com.frameworkium.config.WebDriverWrapper;
import com.frameworkium.listeners.*;
import com.frameworkium.reporting.AllureProperties;
import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.testng.SauceOnDemandAuthenticationProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.SessionId;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import ru.yandex.qatools.allure.Allure;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.TestCaseId;
import ru.yandex.qatools.allure.events.StepFinishedEvent;
import ru.yandex.qatools.allure.events.StepStartedEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Listeners({CaptureListener.class, ScreenshotListener.class, MethodInterceptor.class, SauceLabsListener.class,
        TestListener.class, ResultLoggerListener.class})

public abstract class BaseTest implements SauceOnDemandSessionIdProvider, SauceOnDemandAuthenticationProvider {

    private static ThreadLocal<Boolean> requiresReset;
    private static ThreadLocal<ScreenshotCapture> capture;
    private static ThreadLocal<DriverType> driverType;
    private static List<DriverType> activeDriverTypes = new ArrayList<>();
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
                DriverType driverType = new DriverSetup()
                        .returnDesiredDriverType();
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
    }

    /**
     * The methods which configure the browser once a test runs
     *  - Maximises browser based on the driver type
     *  - Initialises screenshot capture if needed
     *  - Clears the session if another test ran prior
     *  - Sets the user agent of the browser
     *
     * @param testMethod - The test method name of the test
     */
    @BeforeMethod(alwaysRun = true)
    public static void configureBrowserBeforeTest(Method testMethod) {
        configureDriverBasedOnParams();
        initialiseNewScreenshotCapture(testMethod);
    }

    /**
     * Initialise the screenshot capture and link to issue/test case id
     *
     * @param testMethod - Test method passed from the test script
     */
    private static void initialiseNewScreenshotCapture(Method testMethod) {
        if (ScreenshotCapture.isRequired()) {
            String testID = "n/a";
            try {
                testID = testMethod.getName();
            } catch (NullPointerException e) {
                logger.debug("No test method defined.");
            }
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
        requiresReset.set(driverType.get().resetBrowser(requiresReset.get()));
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
        try {
            for (DriverType driverType : activeDriverTypes) {
                driverType.tearDownDriver();
            }
        } catch (Exception e) {
            logger.warn("Session quit unexpectedly.", e);
        }
    }

    /**
     * Creates the allure properties for the report, after the test run
     */
    @AfterSuite(alwaysRun = true)
    public static void createAllureProperties() {
        AllureProperties.create();
    }

    /** @return the Job id for the current thread */
    @Override
    public String getSessionId() {
        WebDriverWrapper driver = getDriver();
        SessionId sessionId = driver.getWrappedRemoteWebDriver().getSessionId();
        return (sessionId == null) ? null : sessionId.toString();
    }

    /**
     * Retrieves the user agent from the browser
     * @return - String of the user agent
     */
    private static String getUserAgent() {
        String ua;
        JavascriptExecutor js = getDriver();
        try {
            ua = (String) js.executeScript("return navigator.userAgent;");
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

    public void stepStart(String stepName){
        Allure.LIFECYCLE.fire(new StepStartedEvent(stepName));
    }

    public void stepFinish(){
        Allure.LIFECYCLE.fire(new StepFinishedEvent());
    }
}
