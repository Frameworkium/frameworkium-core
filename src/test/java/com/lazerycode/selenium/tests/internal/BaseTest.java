package com.lazerycode.selenium.tests.internal;

import static com.lazerycode.selenium.config.DriverType.determineEffectiveDriverType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.lazerycode.selenium.config.DriverType;
import com.lazerycode.selenium.listeners.MethodInterceptor;
import com.lazerycode.selenium.listeners.SauceLabsListener;
import com.lazerycode.selenium.listeners.ScreenshotListener;
import com.lazerycode.selenium.reporting.AllureProperties;
import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.testng.SauceOnDemandAuthenticationProvider;

@Listeners({ScreenshotListener.class, SauceLabsListener.class, MethodInterceptor.class})
public abstract class BaseTest implements SauceOnDemandSessionIdProvider, SauceOnDemandAuthenticationProvider {

    private static List<WebDriver> webDriverPool = Collections
            .synchronizedList(new ArrayList<WebDriver>());
    private static ThreadLocal<WebDriver> driverThread;

    @BeforeSuite(alwaysRun = true)
    public static void instantiateDriverObject() {

        final DriverType desiredDriver = determineEffectiveDriverType(System
                .getProperty("browser"));

        driverThread = new ThreadLocal<WebDriver>() {
            @Override
            protected WebDriver initialValue() {
                final WebDriver webDriver = desiredDriver
                        .configureDriverBinaryAndInstantiateWebDriver();
                webDriverPool.add(webDriver);
                return webDriver;
            }
        };
    }

    public static WebDriver getDriver() {
        return driverThread.get();
    }

    @AfterMethod(alwaysRun = true)
    public static void clearCookies() {
        getDriver().manage().deleteAllCookies();
    }

    @AfterSuite(alwaysRun = true)
    public static void closeDriverObject() {
        for (WebDriver driver : webDriverPool) {
            driver.quit();
        }
        AllureProperties.create();
    }
    
    /**
    *
    * @return the Sauce Job id for the current thread
    */
    @Override
    public String getSessionId() {
        SessionId sessionId = ((RemoteWebDriver)getDriver()).getSessionId();
        return (sessionId == null) ? null : sessionId.toString();
    }
    
    /**
    *
    * @return the {@link SauceOnDemandAuthentication} instance containing the Sauce username/access key
    */
   @Override
   public SauceOnDemandAuthentication getAuthentication() {
       return new SauceOnDemandAuthentication();
   }
}