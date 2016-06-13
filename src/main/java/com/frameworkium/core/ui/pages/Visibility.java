package com.frameworkium.core.ui.pages;

import com.frameworkium.core.ui.annotations.*;
import com.frameworkium.core.ui.driver.WebDriverWrapper;
import com.frameworkium.core.ui.tests.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.TypifiedElement;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfAllElements;
import static ru.yandex.qatools.htmlelements.utils.HtmlElementUtils.*;

/**
 * All things Frameworkium-related dealing with PageObject element visibility.
 */
public final class Visibility {

    private static final List<Class<? extends Annotation>> VISIBILITY_ANNOTATION_CLASSES =
            Arrays.asList(Visible.class, Invisible.class, ForceVisible.class);

    private final Wait<WebDriver> wait;
    private final JavascriptExecutor driver;

    /**
     * Uses the {@link Wait} and {@link WebDriverWrapper} from {@link BaseTest}.
     */
    public Visibility() {
        this(BaseTest.getWait(), BaseTest.getDriver());
    }

    /**
     * @param wait   a specific wait to use.
     * @param driver a specific {@link JavascriptExecutor} to use.
     */
    public Visibility(Wait<WebDriver> wait, JavascriptExecutor driver) {
        this.wait = wait;
        this.driver = driver;
    }

    /**
     * Custom wait which fills the gap left by Selenium whereby
     * <code>not({@link ExpectedConditions#visibilityOf(WebElement)})</code>
     * will fail if the element is not present, but
     * {@link ExpectedConditions#invisibilityOfElementLocated(By)}
     * waits for either not visibility or not present.
     *
     * @param element the element to wait for
     * @return an {@link ExpectedCondition} which returns <strong>false</strong>
     * iff the element is visible, otherwise <strong>true</strong>.
     */
    public static ExpectedCondition<Boolean> notPresentOrInvisible(
            WebElement element) {
        return driver -> {
            try {
                return !element.isDisplayed();
            } catch (NoSuchElementException
                    | StaleElementReferenceException e) {
                return true;
            }
        };
    }

    /**
     * Overloaded {@link Visibility#notPresentOrInvisible(WebElement)}
     * for {@link List} of {@link WebElement}s.
     *
     * @param elements the list of {@link WebElement}s to wait for
     * @return an {@link ExpectedCondition} which returns <strong>false</strong>
     * iff any element is visible, otherwise <strong>true</strong>.
     * @see Visibility#notPresentOrInvisible(WebElement)
     */
    public static ExpectedCondition<List<WebElement>> notPresentOrInvisible(
            List<WebElement> elements) {

        return driver ->
                elements.stream()
                        .noneMatch(WebElement::isDisplayed)
                        ? elements
                        : null;
    }

    /**
     * The main entry point for {@link BasePage}.
     * For each Field:
     * <ul>
     * <li>Ensures either 0 or 1 Frameworkium Visibility annotations are used.</li>
     * <li>Waits for the (in)visibility of elements annotated by visibility annotations.</li>
     * </ul>
     *
     * @param pageObject the "page object" e.g. extends {@link BasePage}
     *                   or {@link TypifiedElement} or {@link HtmlElement}.
     */
    public void waitForAnnotatedElementVisibility(Object pageObject) {

        Field[] allFields = pageObject.getClass().getDeclaredFields();
        Arrays.stream(allFields)
                .filter(this::validateFieldVisibilityAnnotations)
                .forEach(field ->
                        invokeWaitFunctionForField(field, pageObject));
    }

    private boolean validateFieldVisibilityAnnotations(Field field) {
        long annotationCount = visibilityAnnotationsFrom(field).count();

        if (annotationCount > 1) {
            throw new IllegalArgumentException(String.format(
                    "Field %s on %s has too many Visibility related Annotations",
                    field.getName(),
                    field.getDeclaringClass().getName()));
        } else {
            return annotationCount == 1;
        }
    }

    private Stream<Class<? extends Annotation>> visibilityAnnotationsFrom(Field field) {
        return VISIBILITY_ANNOTATION_CLASSES.stream()
                .filter(field::isAnnotationPresent);
    }

    private void invokeWaitFunctionForField(Field field, Object pageObject) {

        Class<? extends Annotation> visibilityAnnotationClass =
                visibilityAnnotationsFrom(field)
                        .findAny()
                        .orElseThrow(IllegalStateException::new);

        if (Visible.class.equals(visibilityAnnotationClass)) {
            waitForFieldToBeVisible(pageObject, field);
        } else if (Invisible.class.equals(visibilityAnnotationClass)) {
            waitForFieldToBeInvisible(pageObject, field);
        } else if (ForceVisible.class.equals(visibilityAnnotationClass)) {
            forceThenWaitForFieldToBeVisible(pageObject, field);
        }
    }

    /**
     * Checks for visibility of Fields with the {@link Visible} annotation.
     * Will recurse inside {@link HtmlElement}s
     *
     * @param pageObject the pageObject
     * @param field      wait for visibility of the field
     */
    @SuppressWarnings("unchecked")
    private void waitForFieldToBeVisible(Object pageObject, Field field) {

        Object objectFromField = getObjectFromField(pageObject, field);
        applyToWebElements(
                field,
                objectFromField,
                we -> wait.until(visibilityOf(we)),
                list -> wait.until(visibilityOfAllElements(list)));

        // recurse inside HtmlElements
        if (isHtmlElementList(field)) {
            ((List<HtmlElement>) objectFromField)
                    .forEach(this::waitForAnnotatedElementVisibility);
        } else if (isHtmlElement(field)) {
            waitForAnnotatedElementVisibility(objectFromField);
        }
    }

    /** Same as waitForFieldToBeVisible but for Invisibility. */
    private void waitForFieldToBeInvisible(Object pageObject, Field field) {

        applyToWebElements(
                field,
                getObjectFromField(pageObject, field),
                we -> wait.until(notPresentOrInvisible(we)),
                list -> wait.until(notPresentOrInvisible(list)));
    }

    /**
     * Calls {@link Visibility#forceVisible(WebElement)} after calling
     * {@link Visibility#waitForFieldToBeVisible(Object, Field)}.
     */
    private void forceThenWaitForFieldToBeVisible(Object pageObject, Field field) {

        applyToWebElements(
                field,
                getObjectFromField(pageObject, field),
                this::forceVisible,
                list -> list.forEach(this::forceVisible));

        waitForFieldToBeVisible(pageObject, field);
    }

    @SuppressWarnings("unchecked")
    private void applyToWebElements(Field field, Object obj, Consumer<WebElement> fun, Consumer<List<WebElement>> listFun) {

        if (isWebElementList(field) || isTypifiedElementList(field) || isHtmlElementList(field)) {
            listFun.accept((List<WebElement>) obj);
        } else if (obj instanceof WebElement) {
            fun.accept((WebElement) obj);
        } else {
            throw new IllegalArgumentException(
                    "Only elements of type HtmlElement, TypifiedElement, WebElement or " +
                            "Lists thereof are supported by Visibility annotations.");
        }
    }

    private Object getObjectFromField(Object pageObject, Field field) {
        field.setAccessible(true);
        try {
            return field.get(pageObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes JavaScript in an attempt to make the element visible
     * e.g. for elements which are occluded but are required for interaction.
     * <p>
     * To apply this to a list of WebElements, try the following:
     * <code>elements.forEach(Visibility::forceVisible)</code>
     *
     * @param element the {@link WebElement} to make visible
     */
    public void forceVisible(WebElement element) {
        driver.executeScript(
                "arguments[0].style.zindex='10000';" +
                        "arguments[0].style.visibility='visible';" +
                        "arguments[0].style.opacity='100';",
                element);
    }
}
