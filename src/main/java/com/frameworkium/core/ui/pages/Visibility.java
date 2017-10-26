package com.frameworkium.core.ui.pages;

import com.frameworkium.core.ui.ExtraExpectedConditions;
import com.frameworkium.core.ui.annotations.*;
import com.frameworkium.core.ui.driver.WebDriverWrapper;
import com.frameworkium.core.ui.tests.BaseTest;
import javassist.Modifier;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Wait;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
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
     * Constructor with a specific wait and JavaScript executor to use.
     *
     * @param wait   a specific wait to use.
     * @param driver a specific {@link JavascriptExecutor} to use.
     */
    public Visibility(Wait<WebDriver> wait, JavascriptExecutor driver) {
        this.wait = wait;
        this.driver = driver;
    }

    /**
     * The main entry point for {@link BasePage}.
     * For each Field:
     * <ul>
     * <li>Ensures either 0 or 1 Frameworkium Visibility annotations are present.</li>
     * <li>Waits for the (in)visibility of elements annotated by annotations.</li>
     * </ul>
     *
     * @param pageObject the "page object" i.e. extends {@link BasePage} or {@link HtmlElement}.
     */
    public void waitForAnnotatedElementVisibility(Object pageObject) {

        getDeclaredFieldsIncludingSuperClasses(pageObject.getClass())
                .stream()
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(this::validateFieldVisibilityAnnotations)
                .forEach(field -> invokeWaitFunctionForField(field, pageObject));
    }

    private List<Field> getDeclaredFieldsIncludingSuperClasses(Class<?> clazz) {
        final List<Field> fields = new ArrayList<>();

        // Get any declared fields from super classes
        // i.e. when a page object extends a custom class which itself extends HtmlElement
        for (Class<?> c = clazz;
                ((c != null) && (c != BasePage.class) && (c != HtmlElement.class));
                c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
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
                we -> wait.until(ExtraExpectedConditions.notPresentOrInvisible(we)),
                list -> wait.until(ExtraExpectedConditions.notPresentOrInvisible(list)));
    }

    /**
     * Calls {@link Visibility#forceVisible(WebElement)} for each ForceVisible
     * field then {@link Visibility#waitForFieldToBeVisible(Object, Field)}.
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
    private void applyToWebElements(
            Field field,
            Object objectFromField,
            Consumer<WebElement> fun,
            Consumer<List<WebElement>> listFun) {

        if (isWebElementList(field) || isTypifiedElementList(field) || isHtmlElementList(field)) {
            listFun.accept((List<WebElement>) objectFromField);
        } else if (objectFromField instanceof WebElement) {
            fun.accept((WebElement) objectFromField);
        } else {
            throw new IllegalArgumentException(
                    "Only elements of type HtmlElement, TypifiedElement, WebElement or "
                            + "Lists thereof are supported by Visibility annotations.");
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
     * To apply this to a list of WebElements, try the following:
     * <code>elements.forEach(Visibility::forceVisible)</code>
     *
     * @param element the {@link WebElement} to make visible
     */
    public void forceVisible(WebElement element) {
        driver.executeScript(
                "arguments[0].style.zindex='10000';"
                        + "arguments[0].style.visibility='visible';"
                        + "arguments[0].style.opacity='100';",
                element);
    }
}
