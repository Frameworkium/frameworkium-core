package com.frameworkium.core.ui.pages;

import com.frameworkium.core.common.reporting.allure.AllureLogger;
import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.capture.ScreenshotCapture;
import com.frameworkium.core.ui.capture.model.Command;
import com.frameworkium.core.ui.driver.Driver;
import com.frameworkium.core.ui.js.JavascriptWait;
import com.frameworkium.core.ui.tests.BaseUITest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;
import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

public abstract class BasePage<T extends BasePage<T>> {

    protected final Logger logger = LogManager.getLogger(this);
    protected final WebDriver driver;
    protected Wait<WebDriver> wait;
    /** Visibility with current page wait and driver. */
    protected Visibility visibility;
    protected JavascriptWait javascriptWait;

    public BasePage() {
        driver = BaseUITest.getDriver();
        wait = BaseUITest.getWait();
        visibility = new Visibility(wait, BaseUITest.getDriver());
        javascriptWait = new JavascriptWait(driver, wait);
    }

    /**
     * Get the current page object. Useful for e.g.
     * <code>myPage.get().then().doSomething();</code>
     *
     * @return the current page object
     */
    @SuppressWarnings("unchecked")
    public T then() {
        return (T) this;
    }

    /**
     * Get current page object. Useful for e.g.
     * <code>myPage.get().then().with().aComponent().clickHome();</code>
     *
     * @return the current page object
     */
    @SuppressWarnings("unchecked")
    public T with() {
        return (T) this;
    }

    /**
     * Get new instance of a PageObject of type T, see {@link BasePage#get()}.
     *
     * @param url the url to open before initialising
     * @return PageObject of type T
     * @see BasePage#get()
     */
    public T get(String url) {
        driver.get(url);
        return get();
    }

    /**
     * Get new instance of a PageObject of type T,
     * see {@link BasePage#get(long)} for updating the timeout.
     *
     * @param url     the url to open before initialising
     * @param timeout the timeout, in seconds, for the new {@link Wait} for this page
     * @return new instance of a PageObject of type T, see {@link BasePage#get()}
     * @deprecated use {@link #get(String, Duration)}
     */
    @Deprecated
    public T get(String url, long timeout) {
        updatePageTimeout(Duration.of(timeout, SECONDS));
        return get(url);
    }

    /**
     * Get new instance of a PageObject of type T,
     * see {@link BasePage#get(long)} for updating the timeout.
     *
     * @param url     the url to open before initialising
     * @param timeout the timeout for the new {@link Wait} for this page
     * @return new instance of a PageObject of type T, see {@link BasePage#get()}
     * @see BasePage#get()
     */
    public T get(String url, Duration timeout) {
        updatePageTimeout(timeout);
        return get(url);
    }

    /**
     * Get new instance of a PageObject of type T.
     *
     * @param timeout the timeout, in seconds, for the new {@link Wait} for this page
     * @return new instance of a PageObject of type T, see {@link BasePage#get()}
     * @deprecated use {@link #get(Duration)}
     */
    @Deprecated
    public T get(long timeout) {
        updatePageTimeout(Duration.of(timeout, SECONDS));
        return get();
    }

    /**
     * Get new instance of a PageObject of type T.
     *
     * @param timeout the timeout, in seconds, for the new {@link Wait} for this page
     * @return new instance of a PageObject of type T, see {@link BasePage#get()}
     * @see BasePage#get()
     */
    public T get(Duration timeout) {
        updatePageTimeout(timeout);
        return get();
    }

    /**
     * Initialises the PageObject.
     * <ul>
     * <li>Initialises fields with lazy proxies</li>
     * <li>Waits for Javascript events including document ready & JS frameworks (if applicable)</li>
     * <li>Processes Frameworkium visibility annotations e.g. {@link Visible}</li>
     * <li>Log page load to Allure and Capture</li>
     * </ul>
     *
     * @return the PageObject, of type T, populated with lazy proxies which are
     *         checked for visibility based upon appropriate Frameworkium annotations.
     */
    @SuppressWarnings("unchecked")
    public T get() {

        HtmlElementLoader.populatePageObject(this, driver);

        // Wait for Elements & JS
        visibility.waitForAnnotatedElementVisibility(this);
        if (!Driver.isNative()) {
            javascriptWait.waitForJavascriptEventsOnLoad();
        }

        // Log
        takePageLoadedScreenshotAndSendToCapture();
        logPageLoadToAllure();

        return (T) this;
    }

    private void updatePageTimeout(Duration timeout) {
        wait = BaseUITest.newWaitWithTimeout(timeout);
        visibility = new Visibility(wait, BaseUITest.getDriver());
        javascriptWait = new JavascriptWait(driver, wait);
    }

    private void logPageLoadToAllure() {
        try {
            AllureLogger.logToAllure("Page '" + getClass().getName() + "' successfully loaded");
        } catch (Exception e) {
            logger.warn("Error logging page load, but loaded successfully", e);
        }
    }

    private void takePageLoadedScreenshotAndSendToCapture() {
        if (ScreenshotCapture.isRequired()) {
            Command pageLoadCommand = new Command(
                    "load", "page", getSimplePageObjectName());
            BaseUITest.getCapture().takeAndSendScreenshot(
                    pageLoadCommand, driver);
        }
    }

    private String getSimplePageObjectName() {
        String packageName = getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1)
                + "."
                + getClass().getSimpleName();
    }

    /**
     * Waits for all JS framework requests to finish on page.
     */
    protected void waitForJavascriptFrameworkToFinish() {
        javascriptWait.waitForJavascriptFramework();
    }

    /**
     * @param javascript the Javascript to execute on the current page
     * @return One of Boolean, Long, String, List or WebElement. Or null.
     * @see JavascriptExecutor#executeScript(String, Object...)
     */
    protected Object executeJS(String javascript) {
        Object returnObj = null;
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        try {
            returnObj = jsExecutor.executeScript(javascript);
        } catch (Exception e) {
            logger.error("Javascript execution failed!");
            logger.debug("Failed Javascript:" + javascript, e);
        }
        return returnObj;
    }

    /**
     * Execute an asynchronous piece of JavaScript in the context of the
     * currently selected frame or window. Unlike executing synchronous
     * JavaScript, scripts executed with this method must explicitly signal they
     * are finished by invoking the provided callback. This callback is always
     * injected into the executed function as the last argument.
     *
     * <p>If executeAsyncScript throws an Exception it's caught and logged.
     *
     * @param javascript the JavaScript code to execute
     * @return One of Boolean, Long, String, List, WebElement, or null.
     * @see JavascriptExecutor#executeAsyncScript(String, Object...)
     */
    protected Object executeAsyncJS(String javascript) {
        Object returnObj = null;
        try {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            returnObj = jsExecutor.executeAsyncScript(javascript);
        } catch (Exception e) {
            logger.error("Async Javascript execution failed!");
            logger.debug("Failed Javascript:\n" + javascript, e);
        }
        return returnObj;
    }

    /** Get title of the web page. */
    public String getTitle() {
        return driver.getTitle();
    }

    /** Get page source code of the current page. */
    public String getSource() {
        return driver.getPageSource();
    }
}
