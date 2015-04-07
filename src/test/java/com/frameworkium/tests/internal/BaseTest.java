package com.frameworkium.tests.internal;

import static com.frameworkium.config.DriverType.determineEffectiveDriverType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

import com.frameworkium.capture.ScreenshotCapture;
import com.frameworkium.config.DriverType;
import com.frameworkium.config.WebDriverWrapper;
import com.frameworkium.listeners.CaptureListener;
import com.frameworkium.listeners.MethodInterceptor;
import com.frameworkium.listeners.SauceLabsListener;
import com.frameworkium.listeners.ScreenshotListener;
import com.frameworkium.listeners.TestListener;
import com.frameworkium.listeners.ZAPIListener;
import com.frameworkium.reporting.AllureProperties;
import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.testng.SauceOnDemandAuthenticationProvider;

@Listeners({CaptureListener.class, ScreenshotListener.class, MethodInterceptor.class, SauceLabsListener.class,
        TestListener.class, ZAPIListener.class})
public abstract class BaseTest implements SauceOnDemandSessionIdProvider, SauceOnDemandAuthenticationProvider {

    private static List<WebDriverWrapper> activeDrivers = Collections
            .synchronizedList(new ArrayList<WebDriverWrapper>());
    private static ThreadLocal<WebDriverWrapper> driver;
    private static DriverType desiredDriverType = determineEffectiveDriverType();
    private static ThreadLocal<Boolean> requiresReset;
    private static ThreadLocal<ScreenshotCapture> capture;

    private static Logger logger = LogManager.getLogger(BaseTest.class);

    @BeforeSuite(alwaysRun = true)
    public static void instantiateDriverObject() {
        driver = new ThreadLocal<WebDriverWrapper>() {
            @Override
            protected WebDriverWrapper initialValue() {
                WebDriverWrapper webDriver = desiredDriverType.instantiate();
                activeDrivers.add(webDriver);
                return webDriver;
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

    public static WebDriverWrapper getDriver() {
        return driver.get();
    }

    @BeforeMethod(alwaysRun = true)
    public static void clearSession() {
        // Reset browser or app
        if (requiresReset.get()) {
            try {
                if (DriverType.isNative()) {
                    getDriver().getWrappedAppiumDriver().resetApp();
                } else {
                    getDriver().manage().deleteAllCookies();
                }
            } catch (SessionNotFoundException e) {
                logger.error("Session quit unexpectedly.", e);
            }
        } else {
            requiresReset.set(Boolean.TRUE);
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void initialiseNewScreenshotCapture(Method testMethod) {
        if (ScreenshotCapture.isRequired()) {
            Issue issueAnnotation = testMethod.getAnnotation(Issue.class);
            String testID = issueAnnotation.value();
            capture.set(new ScreenshotCapture(testID, driver.get()));
        }
    }

    @AfterSuite(alwaysRun = true)
    public static void closeDriverObject() {
        for (WebDriver driver : activeDrivers) {
            try {
                driver.quit();
            } catch (Exception e) {
                logger.warn("Session quit unexpectedly.", e);
            }
        }
    }

    @AfterSuite(alwaysRun = true)
    public static void createAllureProperties() {
        AllureProperties.create();
    }

    /** @return the Sauce Job id for the current thread */
    @Override
    public String getSessionId() {
        SessionId sessionId = getDriver().getWrappedRemoteWebDriver().getSessionId();
        return (sessionId == null) ? null : sessionId.toString();
    }

    /** @return the {@link SauceOnDemandAuthentication} instance containing the Sauce username/access key */
    @Override
    public SauceOnDemandAuthentication getAuthentication() {
        return new SauceOnDemandAuthentication();
    }

    public static ScreenshotCapture getCapture() {
        return capture.get();
    }
}
