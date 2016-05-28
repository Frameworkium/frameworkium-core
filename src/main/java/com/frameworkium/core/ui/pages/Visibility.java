package com.frameworkium.core.ui.pages;

import com.frameworkium.core.ui.annotations.*;
import com.frameworkium.core.ui.tests.BaseTest;
import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.TypifiedElement;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfAllElements;
import static ru.yandex.qatools.htmlelements.utils.HtmlElementUtils.*;

/**
 * All things Frameworkium related to dealing with Element visibility.
 */
public final class Visibility {

    private static final Logger logger = LogManager.getLogger(Visibility.class);

    private final Wait<WebDriver> wait;
    private final Map<Class<? extends Annotation>, BiConsumer<Object, Field>> annotationToFunction =
            ImmutableMap.of(
                    Visible.class, this::waitForFieldToBeVisible,
                    Invisible.class, this::waitForFieldToBeInvisible,
                    ForceVisible.class, this::forceThenWaitForFieldToBeVisible);

    /** Uses the default {@link Wait} from {@link BaseTest}. */
    public Visibility() {
        this.wait = BaseTest.getWait();
    }

    /** @param wait a specific wait to use instead of the default. */
    public Visibility(Wait<WebDriver> wait) {
        this.wait = wait;
    }

    private boolean validateFieldVisibilityAnnotations(Field field) {
        long annotationCount = getAnnotationsFromField(field).count();

        if (annotationCount > 1) {
            throw new IllegalArgumentException(String.format(
                    "Field %s on %s has too many Visibility related Annotations",
                    field.getName(),
                    field.getDeclaringClass().getName()));
        } else {
            return annotationCount == 1;
        }
    }

    private Stream<Class<? extends Annotation>> getAnnotationsFromField(Field field) {
        return annotationToFunction.keySet().stream()
                .filter(field::isAnnotationPresent);
    }

    /** Same as waitForFieldToBeVisible but for Invisibility. */
    private void waitForFieldToBeInvisible(Object pageObject, Field field) {

        Object obj = getObjectFromField(pageObject, field);
        applyToWebElements(
                field,
                obj,
                we -> wait.until(notPresentOrInvisibilityOfElement(we)),
                list -> wait.until(notPresentOrInvisibilityOfElement(list)));
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

    @SuppressWarnings("unchecked")
    private void applyToWebElements(Field field, Object obj, Consumer<WebElement> fun, Consumer<List<WebElement>> listFun) {

        // lists
        if (isHtmlElementList(field)) {
            listFun.accept(
                    ((List<HtmlElement>) obj).stream()
                            .map(HtmlElement::getWrappedElement)
                            .collect(toList()));
        } else if (isTypifiedElementList(field)) {
            listFun.accept(
                    ((List<TypifiedElement>) obj).stream()
                            .map(TypifiedElement::getWrappedElement)
                            .collect(toList()));
        } else if (isWebElementList(field)) {
            listFun.accept((List<WebElement>) obj);
            // single elements
        } else if (isHtmlElement(field)) {
            fun.accept(((HtmlElement) obj).getWrappedElement());
        } else if (isTypifiedElement(field)) {
            fun.accept(((TypifiedElement) obj).getWrappedElement());
        } else if (isWebElement(field)) {
            fun.accept((WebElement) obj);
        } else {
            throw new IllegalArgumentException(
                    "Only elements of type HtmlElement, TypifiedElement, WebElement or " +
                            "Lists thereof are supported by Visibility annotations.");
        }
    }

    /**
     * Custom wait which fills the gap left by Selenium whereby
     * <code>not(visibilityOf(webElement))</code> will fail if the element is
     * not present, but <code>invisibilityOfElementLocated(locator)</code> waits
     * for either not visibility or not presence.
     *
     * @param element the element to wait for
     * @return an {@link ExpectedCondition} which returns false iff the element
     * is visible otherwise false.
     */
    public static ExpectedCondition<Boolean> notPresentOrInvisibilityOfElement(
            WebElement element) {
        return (driver) -> {
            try {
                return !element.isDisplayed();
            } catch (NoSuchElementException
                    | StaleElementReferenceException e) {
                return true;
            }
        };
    }

    /**
     * Overloaded method for {@link List} of {@link WebElement}s.
     *
     * @param elements the list of {@link WebElement}s to wait for
     * @return an {@link ExpectedCondition} which returns false iff the elements
     * are visible otherwise false.
     */
    public static ExpectedCondition<Boolean> notPresentOrInvisibilityOfElement(
            List<WebElement> elements) {
        return (d) -> {
            boolean listIsEmpty = elements.size() == 0;
            boolean atLeastOneElementIsDisplayed =
                    elements.stream()
                            .filter(WebElement::isDisplayed)
                            .findAny()
                            .isPresent();
            return listIsEmpty || !atLeastOneElementIsDisplayed;
        };
    }

    /**
     * Same as waitForFieldToBeVisible but forces visibility before waiting
     */
    private void forceThenWaitForFieldToBeVisible(Object pageObject, Field field) {

        applyToWebElements(
                field,
                getObjectFromField(pageObject, field),
                Visibility::forceVisible,
                Visibility::forceVisible);

        waitForFieldToBeVisible(pageObject, field);
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

        Object objectFromField = getObjectFromField(pageObject, field);

        // Recursively checks the HtmlElement Component(s)
        if (isHtmlElementList(field)) {
            ((List<HtmlElement>) objectFromField)
                    .forEach(this::waitForAnnotatedElementVisibility);
        } else if (isHtmlElement(field)) {
            waitForAnnotatedElementVisibility(objectFromField);
        } else {
            applyToWebElements(
                    field,
                    objectFromField,
                    we -> wait.until(visibilityOf(we)),
                    list -> wait.until(visibilityOfAllElements(list)));
        }
    }

    /**
     * The main entry point for {@link BasePage}.
     * <p>
     * Waits for the (in)visibility of elements annotated by Frameworkium's visibility annotations
     *
     * @param pageObject the page object like object e.g. ? extends {@link BasePage}
     *                   or a {@link TypifiedElement} or an {@link HtmlElement}.
     */
    public void waitForAnnotatedElementVisibility(Object pageObject) {

        Field[] allFields = pageObject.getClass().getDeclaredFields();
        Arrays.stream(allFields)
                .filter(this::validateFieldVisibilityAnnotations)
                .forEach(field ->
                        visibilityFunctionForField(field, pageObject));
    }

    private void visibilityFunctionForField(Field field, Object pageObject) {

        Class<? extends Annotation> annotationClass =
                getAnnotationsFromField(field)
                        .findAny()
                        .orElseThrow(IllegalStateException::new);

        annotationToFunction.get(annotationClass).accept(pageObject, field);
    }

    public static void forceVisible(WebElement element) {
        JavascriptExecutor jsExecutor = BaseTest.getDriver();
        jsExecutor.executeScript(
                "arguments[0].style.zindex='10000';" +
                        "arguments[0].style.visibility='visible';" +
                        "arguments[0].style.opacity='100';",
                element);
    }

    public static void forceVisible(List<WebElement> elements) {
        elements.forEach(Visibility::forceVisible);
    }
}
