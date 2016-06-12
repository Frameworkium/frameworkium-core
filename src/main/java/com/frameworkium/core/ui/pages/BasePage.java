package com.frameworkium.core.ui.pages;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.common.reporting.allure.AllureLogger;
import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.capture.model.Command;
import com.frameworkium.core.ui.tests.BaseTest;
import com.paulhammant.ngwebdriver.NgWebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;
import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader;

import java.util.Objects;

public abstract class BasePage<T extends BasePage<T>> {

    protected final Logger logger = LogManager.getLogger(this);
    protected final WebDriver driver;
    protected Wait<WebDriver> wait;
    protected Visibility visibility;

    private NgWebDriver ngDriver;

    /** Constructor, initialises all things useful. */
    public BasePage() {
        driver = BaseTest.getDriver();
        wait = BaseTest.getWait();
        visibility = new Visibility(wait, BaseTest.getDriver());
        ngDriver = new NgWebDriver(BaseTest.getDriver());
    }

    /**
     * @return the current page object.
     * Useful for e.g. MyPage.get().then().doSomething();
     */
    @SuppressWarnings("unchecked")
    public T then() {
        return (T) this;
    }

    /**
     * @return the current page object.
     * Useful for e.g. MyPage.get().then().with().aComponent().clickHome();
     */
    @SuppressWarnings("unchecked")
    public T with() {
        return (T) this;
    }

    /**
     * Creates a new {@link Wait} with the specified timeout then calls
     * {@link BasePage#get()}.
     *
     * @param timeout the timeout, in seconds, for the new wait for this page
     * @return new instance of a PageObject of type T, see {@link BasePage#get()}
     */
    public T get(long timeout) {
        wait = BaseTest.newWaitWithTimeout(timeout);
        return get();
    }

    /**
     * Initialises the PageObject.
     * <p>
     * <ul>
     * <li>Initialises fields with lazy proxies</li>
     * <li>Waits for AngularJS requests to finish loading, if present</li>
     * <li>Processes Frameworkium visibility annotations e.g. {@link Visible}</li>
     * <li>Log page load to Allure and Capture</li>
     * </ul>
     *
     * @return the PageObject populated with lazy proxies which are checked for
     * visibility based upon appropriate Frameworkium annotations.
     */
    @SuppressWarnings("unchecked")
    public T get() {

        HtmlElementLoader.populatePageObject(this, driver);

        // wait for page to load
        if (isPageAngularJS()) {
            waitForAngularRequestsToFinish();
        }
        visibility.waitForAnnotatedElementVisibility(this);
        // log page load
        takePageLoadedScreenshotAndSendToCapture();
        logPageLoadToAllure();

        return (T) this;
    }

    private void logPageLoadToAllure() {
        try {
            AllureLogger.logToAllure("Page '" + this.getClass().getName() + "' successfully loaded");
        } catch (Exception e) {
            logger.warn("Error logging page load, but loaded successfully", e);
        }
    }

    private void takePageLoadedScreenshotAndSendToCapture() {
        if (Property.CAPTURE_URL.isSpecified()) {
            try {
                BaseTest.getCapture().takeAndSendScreenshot(
                        new Command("load", null, this.getClass().getName()),
                        driver,
                        null);
            } catch (Exception e) {
                logger.warn("Failed to send loading screenshot to Capture.");
                logger.debug(e);
            }
        }
    }

    private boolean isPageAngularJS() {
        return Objects.equals(executeJS("return typeof angular;"), "object");
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

    /** Method to wait for AngularJS requests to finish on the page */
    protected void waitForAngularRequestsToFinish() {
        ngDriver.waitForAngularRequestsToFinish();
    }

    public T get(String url, long timeout) {
        wait = BaseTest.newWaitWithTimeout(timeout);
        return get(url);
    }

    public T get(String url) {
        driver.get(url);
        return get();
    }

    /**
     * Execute an asynchronous piece of JavaScript in the context of the
     * currently selected frame or window. Unlike executing synchronous
     * JavaScript, scripts executed with this method must explicitly signal they
     * are finished by invoking the provided callback. This callback is always
     * injected into the executed function as the last argument.
     * <p>
     * If executeAsyncScript throws an Exception it's caught and logged
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
            logger.debug("Failed Javascript:" + javascript, e);
        }
        return returnObj;
    }

    /** @return Returns the title of the web page */
    public String getTitle() {
        return driver.getTitle();
    }

    /** @return Returns the source code of the current page */
    public String getSource() {
        return driver.getPageSource();
    }
}
