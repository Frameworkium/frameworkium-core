package com.bootstrapium.tests.internal;

import static com.bootstrapium.config.DriverType.determineEffectiveDriverType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.bootstrapium.config.DriverType;
import com.bootstrapium.listeners.MethodInterceptor;
import com.bootstrapium.listeners.SauceLabsListener;
import com.bootstrapium.listeners.ScreenshotListener;
import com.bootstrapium.reporting.AllureProperties;
import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.testng.SauceOnDemandAuthenticationProvider;

@Listeners({ScreenshotListener.class, MethodInterceptor.class, SauceLabsListener.class})
public abstract class BaseTest implements SauceOnDemandSessionIdProvider, SauceOnDemandAuthenticationProvider {

    private static List<WebDriver> webDriverPool = Collections
            .synchronizedList(new ArrayList<WebDriver>());
    private static ThreadLocal<WebDriver> driverThread;

    private static DriverType desiredDriver = determineEffectiveDriverType();
    
    @BeforeSuite(alwaysRun = true)
    public static void instantiateNonNativeDriverObject() {
    	if(DriverType.isNative()) {
    		driverThread = new ThreadLocal<WebDriver>();
    	} else {
    		driverThread = new ThreadLocal<WebDriver>() {
    			@Override
    			protected WebDriver initialValue() {
    				final WebDriver webDriver = desiredDriver.instantiate();
    				webDriverPool.add(webDriver);
    				return webDriver;
    			}
    		};
    	}
    }

    @BeforeMethod(alwaysRun = true)
    public static void instantiateNativeDriverObject() {
    	if(DriverType.isNative()) {
    		driverThread.set(desiredDriver.instantiate());
    	}
    }
    
    public static WebDriver getDriver() {
        return driverThread.get();
    }

    @AfterMethod(alwaysRun = true)
    public static void clearSession() {
    	if(DriverType.isNative()) {
    		getDriver().quit();
    	} else {
    		getDriver().manage().deleteAllCookies();
    	}
    }

    @AfterSuite(alwaysRun = true)
    public static void closeDriverObject() {
        for (WebDriver driver : webDriverPool) {
            driver.quit();
        }
    }
    
    @AfterSuite(alwaysRun = true)
    public static void createAllureProperties() {
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