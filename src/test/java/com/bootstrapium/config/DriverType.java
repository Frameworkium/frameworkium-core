package com.bootstrapium.config;

import static com.bootstrapium.config.SystemProperty.APP_PATH;
import static com.bootstrapium.config.SystemProperty.BROWSER_VERSION;
import static com.bootstrapium.config.SystemProperty.BUILD;
import static com.bootstrapium.config.SystemProperty.DEVICE_NAME;
import static com.bootstrapium.config.SystemProperty.GRID_URL;
import static com.bootstrapium.config.SystemProperty.PLATFORM;
import static com.bootstrapium.config.SystemProperty.PLATFORM_VERSION;
import io.appium.java_client.AppiumDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import com.bootstrapium.listeners.EventListener;
import com.opera.core.systems.OperaDriver;

public enum DriverType implements DriverSetup {

    FIREFOX {
        public DesiredCapabilities getDesiredCapabilities() {
            return DesiredCapabilities.firefox();
        }

        public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
            return new FirefoxDriver(capabilities);
        }
    },
    CHROME {
        public DesiredCapabilities getDesiredCapabilities() {
            DesiredCapabilities capabilities = DesiredCapabilities.chrome();
            capabilities.setCapability("chrome.switches",
            		Arrays.asList("--no-default-browser-check"));
            HashMap<String, String> chromePreferences = new HashMap<String, String>();
            chromePreferences.put("profile.password_manager_enabled", "false");
            capabilities.setCapability("chrome.prefs", chromePreferences);
            return capabilities;
        }

        public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
            	return new ChromeDriver(capabilities);
        }
    },
    IE {
        public DesiredCapabilities getDesiredCapabilities() {
            DesiredCapabilities capabilities = DesiredCapabilities
                    .internetExplorer();
            capabilities.setCapability(
                    CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION,
                    true);
            capabilities.setCapability(
                    InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true);
            capabilities.setCapability("requireWindowFocus", true);
            return capabilities;
        }

        public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
            return new InternetExplorerDriver(capabilities);
        }
    },
    SAFARI {
        public DesiredCapabilities getDesiredCapabilities() {
            if (isMobile()) {
                DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setCapability("browserName", "safari");
                capabilities.setCapability("platformName", "iOS");
                return capabilities;
            } else {
                DesiredCapabilities capabilities = DesiredCapabilities.safari();
                capabilities.setCapability("safari.cleanSession", true);
                return capabilities;
            }
        }

        public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
            if (isMobile()) {
                throw new IllegalArgumentException(
                        "seleniumGridURL or sauceUser and sauceKey must be specified when running on iOS");
            } else {
                return new SafariDriver(capabilities);
            }
        }
    },
    OPERA {
        public DesiredCapabilities getDesiredCapabilities() {
            DesiredCapabilities capabilities = DesiredCapabilities.opera();
            capabilities.setCapability("opera.arguments", "-nowin -nomail");
            return capabilities;
        }

        public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
            return new OperaDriver(capabilities);
        }
    },
    PHANTOMJS {
        public DesiredCapabilities getDesiredCapabilities() {
            DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
            capabilities.setCapability("takesScreenshot", true);
            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new  String[] {
                    "--webdriver-loglevel=NONE"
            });
            return capabilities;
        }

        public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
            return new PhantomJSDriver(capabilities);
        }
    },
    BROWSER {
        public DesiredCapabilities getDesiredCapabilities() {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("browserName", "Browser");
            capabilities.setCapability("platformName", "Android");
            return capabilities;
        }

        public WebDriver getWebDriverObject(
                DesiredCapabilities desiredCapabilities) {
            throw new IllegalArgumentException(
                    "seleniumGridURL or sauceUser and sauceKey must be specified when running on Android");

        }
    },
    IOS {
        public DesiredCapabilities getDesiredCapabilities() {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("platformName", "iOS");
            return capabilities;
        }

        public WebDriver getWebDriverObject(
                DesiredCapabilities desiredCapabilities) {
            throw new IllegalArgumentException(
                    "seleniumGridURL or sauceUser and sauceKey must be specified when running on iOS");

        }
    },
    ANDROID {
        public DesiredCapabilities getDesiredCapabilities() {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("platformName", "Android");
            return capabilities;
        }

        public WebDriver getWebDriverObject(
                DesiredCapabilities desiredCapabilities) {
            throw new IllegalArgumentException(
                    "seleniumGridURL or sauceUser and sauceKey must be specified when running on Android");

        }
    };

    public static final DriverType defaultDriverType = PHANTOMJS;
    public static final boolean useRemoteWebDriver = GRID_URL.isSpecified()
            || Sauce.isDesired();

    public WebDriver instantiateWebDriver() throws MalformedURLException {
        DesiredCapabilities desiredCapabilities = getDesiredCapabilities();

        if (useRemoteWebDriver) {
        	URL seleniumGridURL;
        	if (Sauce.isDesired()) {
        		seleniumGridURL = Sauce.getURL();
        		// Enable HTML source capture
        		desiredCapabilities.setCapability("capture-html", true);
        		// Disable Sauce Advisor
        		desiredCapabilities.setCapability("sauce-advisor", false);
        		// Set build number
        		desiredCapabilities.setCapability("build", BUILD.getValue());
        		
        		if(isNative()) {
        			// Set app path
        			desiredCapabilities.setCapability("app", "sauce-storage:" + new File(APP_PATH.getValue()).getName());
        		}
        		
            } else {
            	seleniumGridURL = new URL(GRID_URL.getValue());
            }

            if (isMobile()) {
                desiredCapabilities.setCapability("appiumVersion", "1.3.4");

                if (PLATFORM_VERSION.isSpecified()) {
                    desiredCapabilities.setCapability("platformVersion",
                            PLATFORM_VERSION.getValue());
                } else {
                    if(PLATFORM
                            .getValue().equalsIgnoreCase("ios")) {
                        desiredCapabilities.setCapability("platformVersion",
                                "8.1");
                        
                    } else if(PLATFORM
                            .getValue().equalsIgnoreCase("android")) {
                        desiredCapabilities.setCapability("platformVersion",
                                "5.0");
                        
                    }
                }

                if (DEVICE_NAME.isSpecified()) {
                    desiredCapabilities.setCapability("deviceName",
                            DEVICE_NAME.getValue());
                } else {
                    if(PLATFORM
                            .getValue().equalsIgnoreCase("ios")) {
                        desiredCapabilities.setCapability("deviceName",
                                "iPhone Simulator");
                        
                    } else if(PLATFORM
                            .getValue().equalsIgnoreCase("android")) {
                        desiredCapabilities.setCapability("deviceName",
                                "Android Emulator");
                        
                    }
                }

                return new AppiumDriver(seleniumGridURL, desiredCapabilities);
            } else {
                if (PLATFORM.isSpecified()) {
                    desiredCapabilities.setPlatform(Platform.valueOf(PLATFORM
                            .getValue().toUpperCase()));
                }

                if (BROWSER_VERSION.isSpecified()) {
                    desiredCapabilities.setVersion(BROWSER_VERSION.getValue());
                }
                return new RemoteWebDriver(seleniumGridURL, desiredCapabilities);
            }
        }

        if (isMobile()) {
            throw new IllegalArgumentException(
                    "seleniumGridURL or sauce must be specified when running via Appium.");
        } else {
            return getWebDriverObject(desiredCapabilities);
        }
    }

    public static DriverType determineEffectiveDriverType() {
        DriverType driverType = defaultDriverType;
        try {
        	if(isNative()) {
        		driverType = valueOf(PLATFORM.getValue().toUpperCase());
        	} else {
        		driverType = valueOf(com.bootstrapium.config.SystemProperty.BROWSER.getValue().toUpperCase());
        	}
        } catch (IllegalArgumentException ignored) {
            System.err.println("Unknown driver specified, defaulting to '"
                    + driverType + "'...");
        } catch (NullPointerException ignored) {
            System.err.println("No driver specified, defaulting to '"
                    + driverType + "'...");
        }

        return driverType;
    }

    public WebDriver instantiate() {
        System.out.println("Current Browser Selection: " + this);

        try {
            WebDriver driver = instantiateWebDriver();
            EFWebDriver eventFiringWD= new EFWebDriver(driver);
            eventFiringWD.register(new EventListener());
            return eventFiringWD;
        } catch (MalformedURLException urlIsInvalid) {
            urlIsInvalid.printStackTrace();
            return null;
        }
    }

    public static boolean isMobile() {
        return "ios".equalsIgnoreCase(PLATFORM.getValue())
                || "android".equalsIgnoreCase(PLATFORM.getValue());
    }
    
    public static boolean isNative() {
        return APP_PATH.isSpecified();
    }
}