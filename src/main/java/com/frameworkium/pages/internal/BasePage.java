package com.frameworkium.pages.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.paulhammant.ngwebdriver.WaitForAngularRequestsToFinish;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.TypifiedElement;
import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader;

import com.frameworkium.capture.model.Command;
import com.frameworkium.config.SystemProperty;
import com.frameworkium.reporting.AllureLogger;
import com.frameworkium.tests.internal.BaseTest;
import com.google.inject.Inject;

public abstract class BasePage<T extends BasePage<T>> {

    @Inject
    protected WebDriver driver;
    @Inject
    protected WebDriverWait wait;

    protected final Logger logger = LogManager.getLogger(this);

    /**
     * @return Returns the current page object.
     * Useful for e.g. MyPage.get().then().doSomething();
     */
    @SuppressWarnings("unchecked")
    public T then() {
        return (T) this;
    }

    /**
     * @return Returns the current page object.
     * Useful for e.g. MyPage.get().then().with().aComponent().clickHome();
     */
    @SuppressWarnings("unchecked")
    public T with() {
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T get() {
        HtmlElementLoader.populatePageObject(this, driver);
        try {
            if(isPageAngularJS()) {
                waitForAngularRequestsToFinish();
            }
            waitForExpectedVisibleElements(this);
            try {
                AllureLogger.logToAllure("Page '" + this.getClass().getName() + "' successfully loaded");
                if (SystemProperty.CAPTURE_URL.isSpecified())
                    BaseTest.getCapture().takeAndSendScreenshot(
                            new Command("load", null, this.getClass().getName()), driver, null);
            } catch (Exception e) {
                logger.error("Error logging page load, but loaded successfully");
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            logger.error("Error while waiting for page to load", e);
        }
        return (T) this;
    }

    public T get(String url) {
        driver.get(url);
        return get();
    }

    private void waitForExpectedVisibleElements(Object pageObject)
            throws IllegalArgumentException, IllegalAccessException {
        waitForExpectedVisibleElements(pageObject, StringUtils.EMPTY);
    }

    @SuppressWarnings("unchecked")
    private void waitForExpectedVisibleElements(Object pageObject, String visibleGroupName) throws IllegalArgumentException,
            IllegalAccessException {
        for (Field field : pageObject.getClass().getDeclaredFields()) {
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation instanceof Visible) {
                    // If the group name matches, then check for visibility
                    if (((Visible) annotation).value().equalsIgnoreCase(visibleGroupName)) {
                        field.setAccessible(true);
                        Object obj = field.get(pageObject);

                        // This handles Lists of WebElements e.g. List<WebElement>
                        if (obj instanceof List) {
                            //TODO - where to handle List<Link>, for example?
                            try {
                                wait.until(ExpectedConditions.visibilityOfAllElements((List<WebElement>) obj));
                            } catch (StaleElementReferenceException serex) {
                                logger.info("Caught StaleElementReferenceException");
                                tryToEnsureWeHaveUnloadedOldPageAndNewPageIsReady();
                                wait.until(ExpectedConditions.visibilityOfAllElements((List<WebElement>) obj));
                            } catch (ClassCastException ccex) {
                                logger.debug("Caught ClassCastException - will try to get the first object in the List instead");
                                obj = ((List<Object>) obj).get(0);
                                waitForObjectToBeVisible(obj);
                            }
                        }
                        // Otherwise, it's a single object
                        // - HtmlElement, WebElement or TypifiedElement
                        else {
                            waitForObjectToBeVisible(obj);
                        }
                    }
                }
            }
        }
    }

    private void waitForObjectToBeVisible(Object obj) throws IllegalArgumentException, IllegalAccessException {
        // Checks for @Visible tags inside an HtmlElement Component
        if (obj instanceof HtmlElement) {
            logger.debug("Checking for visible elements inside HtmlElement");
            waitForExpectedVisibleElements(obj);
        }

        WebElement element;
        if (obj instanceof TypifiedElement) {
            element = ((TypifiedElement) obj).getWrappedElement();
        } else if (obj instanceof WebElement) {
            element = (WebElement) obj;
        } else {
            throw new IllegalArgumentException(
                    "Only elements of type TypifiedElement, WebElement or " +
                            "List<WebElement> are supported by @Visible.");
        }

        // Retries when an element is looked up before the previous page has unloaded or before doc ready
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
        } catch (StaleElementReferenceException e) {
            logger.info("Caught StaleElementReferenceException");
            tryToEnsureWeHaveUnloadedOldPageAndNewPageIsReady();
            // If the below throws a StaleElementReferenceException try
            // increasing the sleep time or count in:
            // tryToEnsureWeHaveUnloadedOldPageAndNewPageIsReady()
            wait.until(ExpectedConditions.visibilityOf(element));
        }
    }

    /**
     * Will wait up to ~10 seconds for the document to be ready.
     */
    private void tryToEnsureWeHaveUnloadedOldPageAndNewPageIsReady() {
        int notReadyCount = 0;
        int readyCount = 0;
        while (notReadyCount < 20 && readyCount < 3) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) { /* don't care */ }
            boolean docReady = (boolean) executeJS(
                    "return document.readyState == 'complete'");
            if (docReady) {
                readyCount++;
                logger.debug(String.format(
                        "Document ready. Not ready %d times, ready %d times.",
                        notReadyCount, readyCount));
            } else {
                notReadyCount++;
                logger.warn(String.format(
                        "Document not ready. Not ready %d times, ready %d times.",
                        notReadyCount, readyCount));
            }
        }
    }

    /**
     * Executes a javascript snippet to determine whether a page uses AngularJS
     * @return boolean - AngularJS true/false
     */
    private boolean isPageAngularJS() {
        return executeJS("return typeof angular;").equals("object");
    }

    /**
     * @param javascript the Javascript to execute on the current page
     * @return Returns an Object returned by the Javascript provided
     */
    public Object executeJS(String javascript) {
        Object returnObj = null;
        try {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            returnObj = jsExecutor.executeScript(javascript);
        } catch(Exception e) {
            logger.error("Javascript execution failed! Please investigate into the issue.");
            logger.debug("Failed Javascript:" + javascript);
        }
        return returnObj;
    }

    /**
     * Method to wait for AngularJS requests to finish on the page
     */
    public void waitForAngularRequestsToFinish() {
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
        WaitForAngularRequestsToFinish
                .waitForAngularRequestsToFinish((JavascriptExecutor) driver);
    }

    /**
     * Method which executes an async JS call
     *
     * @param javascript the JavaScript code to execute
     * @return the object returned from the executed JavaScript
     */
    public Object executeAsyncJS(String javascript) {
        Object returnObj = null;
        try {
            driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            returnObj = jsExecutor.executeAsyncScript(javascript);
        } catch(Exception e) {
            logger.error("Async javascript execution failed! Please investigate the issue.");
            logger.debug("Failed Javascript:" + javascript);
        }
        return returnObj;
    }

    /**
     * @return Returns the title of the web page
     */
    public String getTitle() {
        return driver.getTitle();
    }

    /**
     * @return Returns the source code of the current page
     */
    public String getSource() {
        return driver.getPageSource();
    }

}