package com.frameworkium.core.ui.tests;

import com.frameworkium.core.common.listeners.MethodInterceptor;
import com.frameworkium.core.common.listeners.ResultLoggerListener;
import com.frameworkium.core.common.listeners.TestListener;
import com.frameworkium.core.common.reporting.allure.AllureLogger;
import com.frameworkium.core.ui.capture.ScreenshotCapture;
import com.frameworkium.core.ui.driver.DriverSetup;
import com.frameworkium.core.ui.driver.DriverType;
import com.frameworkium.core.ui.driver.WebDriverWrapper;
import com.frameworkium.core.ui.listeners.CaptureListener;
import com.frameworkium.core.ui.listeners.SauceLabsListener;
import com.frameworkium.core.ui.listeners.ScreenshotListener;
import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.testng.SauceOnDemandAuthenticationProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.SessionId;
import org.testng.annotations.*;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.TestCaseId;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Listeners({CaptureListener.class, ScreenshotListener.class,
        MethodInterceptor.class, SauceLabsListener.class,
        TestListener.class, ResultLoggerListener.class})
public abstract class BaseTest
        implements SauceOnDemandSessionIdProvider, SauceOnDemandAuthenticationProvider {

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
     *  - Configure the browser based on parameters (maximise window, session resets, user agent)
     */
    @BeforeSuite(alwaysRun = true)
    public static void instantiateDriverObject() {
        driverType = new ThreadLocal<DriverType>() {
            @Override
            protected DriverType initialValue() {
                DriverType driverType =
                        new DriverSetup().returnDesiredDriverType();
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

        //configureBrowserBeforeTest(testMethod);
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
        try {
            configureDriverBasedOnParams();
            initialiseNewScreenshotCapture(testMethod);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    @AfterMethod(alwaysRun = true)
    public static void closeDriverObject() {
        try {
            activeDriverTypes.forEach(DriverType::tearDownDriver);
        } catch (Exception e) {
            logger.warn("Session quit unexpectedly.", e);
        }
    }

    /**
     * Creates the allure properties for the report, after the test run
     */
    @AfterSuite(alwaysRun = true)
    public static void createAllureProperties() {
        com.frameworkium.core.common.reporting.allure.AllureProperties.create();
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

    /**
     * Logs the start of a step to your allure report
     * Other steps will be substeps until you call stepFinish
     * @param stepName the name of the step
     */
    public void __stepStart(String stepName){
        AllureLogger.__stepStart(stepName);
    }

    /**
     * Logs the end of a step
     */
    public void __stepFinish(){
        AllureLogger.__stepFinish();
    }

}
