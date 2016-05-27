package com.frameworkium.core.ui.pages;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.common.reporting.allure.AllureLogger;
import com.frameworkium.core.ui.annotations.ForceVisible;
import com.frameworkium.core.ui.annotations.Invisible;
import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.capture.model.Command;
import com.frameworkium.core.ui.tests.BaseTest;
import com.google.common.collect.ImmutableMap;
import com.paulhammant.ngwebdriver.NgWebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.TypifiedElement;
import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader;
import ru.yandex.qatools.htmlelements.utils.HtmlElementUtils;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfAllElements;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfAllElements;

public abstract class BasePage<T extends BasePage<T>> {

    private static final long DEFAULT_TIMEOUT_SECONDS = 10;

    protected final Logger logger = LogManager.getLogger(this);
    protected final WebDriver driver;
    protected Wait<WebDriver> wait;

    private NgWebDriver ngDriver;

    public BasePage() {
        driver = BaseTest.getDriver();
        wait = newWaitWithTimeout(DEFAULT_TIMEOUT_SECONDS);
        ngDriver = new NgWebDriver((JavascriptExecutor) driver);
    }

    private FluentWait<WebDriver> newWaitWithTimeout(long timeout) {
        return new FluentWait<>(driver)
                .withTimeout(timeout, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

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

    /**
     * Initialises the PageObject.
     * <p>
     * <ul>
     * <li>Initialises fields with lazy proxies</li>
     * <li>Waits for AngularJS requests to finish loading, if present</li>
     * <li>Processes Frameworkium visibility annotations e.g. {@link Visible}</li>
     * <li>Log page load to Allure and Capture</li>
     * </ul>
     */
    @SuppressWarnings("unchecked")
    public T get() {
        HtmlElementLoader.populatePageObject(this, driver);
        if (isPageAngularJS()) {
            waitForAngularRequestsToFinish();
        }

        processFrameworkiumVisibilityAnnotations(this);

        try {
            AllureLogger.logToAllure("Page '" + this.getClass().getName() + "' successfully loaded");
            takePageLoadedScreenshotAndSendToCapture();
        } catch (Exception e) {
            logger.warn("Error logging page load, but loaded successfully", e);
        }
        return (T) this;
    }

    public T get(String url) {
        driver.get(url);
        return get();
    }

    public T get(long timeout) {
        wait = newWaitWithTimeout(timeout);
        return get();
    }

    public T get(String url, long timeout) {
        wait = newWaitWithTimeout(timeout);
        return get(url);
    }

    // TODO: move all Frameworkium visibility annotation code to separate class
    private void processFrameworkiumVisibilityAnnotations(Object pageObject) {

        Field[] allFields = pageObject.getClass().getDeclaredFields();

        Arrays.stream(allFields)
                .filter(this::validateFieldVisibilityAnnotations)
                .forEach(field -> visibilityFunctionForField(field).accept(pageObject, field));
    }

    private BiConsumer<Object, Field> visibilityFunctionForField(Field field) {
        Map<Class<? extends Annotation>, BiConsumer<Object, Field>> annotationToFunction =
                ImmutableMap.of(
                        Visible.class, this::waitForFieldToBeVisible,
                        Invisible.class, this::waitForFieldToBeInvisible,
                        ForceVisible.class, this::forceThenWaitForFieldToBeVisible);

        return annotationToFunction.get(
                getAnnotationFromFieldStream(field)
                        .findAny()
                        .orElseThrow(IllegalStateException::new));
    }

    private Stream<Class<? extends Annotation>> getAnnotationFromFieldStream(Field field) {
        return Stream.of(Visible.class, Invisible.class, ForceVisible.class)
                .filter(field::isAnnotationPresent);
    }

    private boolean validateFieldVisibilityAnnotations(Field field) {
        long annotationCount =
                getAnnotationFromFieldStream(field)
                        .count();

        if (annotationCount > 1) {
            throw new IllegalArgumentException(String.format(
                    "Field %s on %s has too many Visibility related Annotations",
                    field.getName(),
                    field.getDeclaringClass().getName()));
        } else {
            return annotationCount == 1;
        }
    }

    private Object getObjectFromField(Object pageObject, Field field) {
        field.setAccessible(true);
        try {
            return field.get(pageObject);
        } catch (IllegalAccessException e) {
            logger.error(
                    String.format(
                            "Error while accessing field %s on page %s",
                            field.getName(),
                            pageObject.getClass().getName()),
                    e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks for visibility of Fields with the {@link Visible} annotation.
     * Can recurse inside {@link HtmlElement}s
     *
     * @param pageObject the pageObject
     * @param field      wait for visibility of the field
     */
    @SuppressWarnings("unchecked")
    private void waitForFieldToBeVisible(Object pageObject, Field field) {

        Object obj = getObjectFromField(pageObject, field);

        if (HtmlElementUtils.isHtmlElementList(field)) {
            // Recursively checks the HtmlElement Component
            ((List<HtmlElement>) obj)
                    .forEach(this::processFrameworkiumVisibilityAnnotations);
        } else if (HtmlElementUtils.isHtmlElement(field)) {
            processFrameworkiumVisibilityAnnotations(obj);
        } else if (HtmlElementUtils.isTypifiedElementList(field)) {
            List<WebElement> webElements =
                    ((List<TypifiedElement>) obj).stream()
                            .map(TypifiedElement::getWrappedElement)
                            .collect(toList());
            wait.until(visibilityOfAllElements(webElements));
        } else if (HtmlElementUtils.isTypifiedElement(field)) {
            wait.until(visibilityOf(((TypifiedElement) obj).getWrappedElement()));
        } else if (HtmlElementUtils.isWebElementList(field)) {
            wait.until(visibilityOfAllElements((List<WebElement>) obj));
        } else if (HtmlElementUtils.isWebElement(field)) {
            wait.until(visibilityOf((WebElement) obj));
        } else {
            throw new IllegalArgumentException(
                    "Only elements of type HtmlElement, TypifiedElement, WebElement or " +
                            "Lists thereof are supported by @Visible.");
        }
    }

    /**
     * Same as waitForFieldToBeVisible but for Invisibility i.e. not visible
     */
    @SuppressWarnings("unchecked")
    private void waitForFieldToBeInvisible(Object pageObject, Field field) {

        Object obj = getObjectFromField(pageObject, field);
        if (HtmlElementUtils.isHtmlElementList(field)) {
            ((List<HtmlElement>) obj)
                    .forEach(he -> {
                        WebElement we = he.getWrappedElement();
                        wait.until(notPresentOrInvisibilityOfElement(we));
                    });
        } else if (HtmlElementUtils.isHtmlElement(field)) {
            wait.until(notPresentOrInvisibilityOfElement(((HtmlElement) obj).getWrappedElement()));
        } else if (HtmlElementUtils.isTypifiedElementList(field)) {
            ((List<TypifiedElement>) obj).stream()
                    .map(TypifiedElement::getWrappedElement)
                    .forEach(e -> wait.until(notPresentOrInvisibilityOfElement(e)));
        } else if (HtmlElementUtils.isTypifiedElement(field)) {
            wait.until(notPresentOrInvisibilityOfElement(((TypifiedElement) obj).getWrappedElement()));
        } else if (HtmlElementUtils.isWebElementList(field)) {
            wait.until(invisibilityOfAllElements((List<WebElement>) obj));
        } else if (HtmlElementUtils.isWebElement(field)) {
            wait.until(notPresentOrInvisibilityOfElement((WebElement) obj));
        } else {
            throw new IllegalArgumentException(
                    "Only elements of type HtmlElement, TypifiedElement, WebElement or " +
                            "Lists thereof are supported by @Invisible.");
        }
    }

    private ExpectedCondition<Boolean> notPresentOrInvisibilityOfElement(WebElement element) {
        return new ExpectedCondition<Boolean>() {
            @Nullable
            @Override
            public Boolean apply(@Nullable WebDriver input) {
                try {
                    return !element.isDisplayed();
                } catch (NoSuchElementException
                        | StaleElementReferenceException e) {
                    return true;
                }
            }
        };
    }

    /**
     * Same as waitForFieldToBeVisible but forces visibility before waiting
     */
    @SuppressWarnings("unchecked")
    private void forceThenWaitForFieldToBeVisible(Object pageObject, Field field) {

        Object obj = getObjectFromField(pageObject, field);

        if (HtmlElementUtils.isHtmlElementList(field)) {
            ((List<HtmlElement>) obj).stream()
                    .map(HtmlElement::getWrappedElement)
                    .forEach(this::forceVisible);
        } else if (HtmlElementUtils.isHtmlElement(field)) {
            forceVisible(((HtmlElement) obj).getWrappedElement());
        } else if (HtmlElementUtils.isTypifiedElementList(field)) {
            ((List<TypifiedElement>) obj).stream()
                    .map(TypifiedElement::getWrappedElement)
                    .forEach(this::forceVisible);
        } else if (HtmlElementUtils.isTypifiedElement(field)) {
            forceVisible(((TypifiedElement) obj).getWrappedElement());
        } else if (HtmlElementUtils.isWebElementList(field)) {
            ((List<WebElement>) obj).stream()
                    .forEach(this::forceVisible);
        } else if (HtmlElementUtils.isWebElement(field)) {
            forceVisible((WebElement) obj);
        } else {
            throw new IllegalArgumentException(
                    "Only elements of type HtmlElement, TypifiedElement, WebElement or " +
                            "Lists thereof are supported by @ForceVisible.");
        }

        waitForFieldToBeVisible(pageObject, field);
    }

    private void takePageLoadedScreenshotAndSendToCapture() {
        if (Property.CAPTURE_URL.isSpecified()) {
            try {
                BaseTest.getCapture().takeAndSendScreenshot(
                        new Command("load", null, this.getClass().getName()),
                        driver,
                        null);
            } catch (Exception e) {
                logger.warn("Failed to ");
            }
        }
    }

    /**
     * Executes a javascript snippet to determine whether a page uses AngularJS
     *
     * @return boolean - AngularJS true/false
     */
    private boolean isPageAngularJS() {
        try {
            return executeJS("return typeof angular;").equals("object");
        } catch (NullPointerException e) {
            logger.error("Detecting whether the page was angular returned a null object. " +
                    "This means your browser hasn't started! Investigate into the issue.");
            throw new RuntimeException(e);
        }
    }

    /** Method to wait for AngularJS requests to finish on the page */
    protected void waitForAngularRequestsToFinish() {
        ngDriver.waitForAngularRequestsToFinish();
    }

    /**
     * @param javascript the Javascript to execute on the current page
     * @return Returns an Object returned by the Javascript provided
     */
    protected Object executeJS(String javascript) {
        Object returnObj = null;
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        try {
            returnObj = jsExecutor.executeScript(javascript);
        } catch (Exception e) {
            logger.error("Javascript execution failed! Please investigate into the issue.");
            logger.debug("Failed Javascript:" + javascript);
        }
        return returnObj;
    }

    /**
     * Method which executes an async JS call
     *
     * @param javascript the JavaScript code to execute
     * @return the object returned from the executed JavaScript
     */
    protected Object executeAsyncJS(String javascript) {
        Object returnObj = null;
        try {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            returnObj = jsExecutor.executeAsyncScript(javascript);
        } catch (Exception e) {
            logger.error("Async javascript execution failed! Please investigate the issue.");
            logger.debug("Failed Javascript:" + javascript);
        }
        return returnObj;
    }

    protected void forceVisible(WebElement element) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript(
                "arguments[0].style.zindex='10000';" +
                        "arguments[0].style.visibility='visible';" +
                        "arguments[0].style.opacity='100';",
                element);
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
