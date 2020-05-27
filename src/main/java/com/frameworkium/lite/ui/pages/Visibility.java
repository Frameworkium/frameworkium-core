package com.frameworkium.lite.ui.pages;

import com.frameworkium.lite.ui.ExtraExpectedConditions;
import com.frameworkium.lite.ui.annotations.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Wait;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfAllElements;
import static ru.yandex.qatools.htmlelements.utils.HtmlElementUtils.*;

/**
 * All things Frameworkium-related dealing with PageObject element visibility.
 */
public final class Visibility {

    public static final String FORCE_VISIBLE_SCRIPT =
            "arguments[0].style.zindex='10000';"
                    + "arguments[0].style.visibility='visible';"
                    + "arguments[0].style.opacity='100';";

    private static final List<Class<? extends Annotation>> VISIBILITY_ANNOTATION_CLASSES =
            Arrays.asList(Visible.class, Invisible.class, ForceVisible.class);

    private final Wait<WebDriver> wait;
    private final JavascriptExecutor javascriptExecutor;

    public Visibility(Wait<WebDriver> wait, JavascriptExecutor driver) {
        this.wait = wait;
        this.javascriptExecutor = driver;
    }

    /**
     * The main entry point for {@link BasePage}.
     * For each Field:
     * <ul>
     * <li>Ensures either 0 or 1 Frameworkium Visibility annotations are present.</li>
     * <li>Waits for the (in)visibility of elements based upon annotations.</li>
     * </ul>
     *
     * @param pageObject the "page object" i.e. extends {@link BasePage} or {@link HtmlElement}.
     */
    void waitForAnnotatedElementVisibility(Object pageObject) {

        getDeclaredFieldsIncludingSuperClasses(pageObject.getClass())
                .stream()
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(this::hasOnlyOneVisibilityAnnotation)
                .forEach(field -> invokeWaitFunctionForField(field, pageObject));
    }

    /**
     * Extracts all fields from a class (page object) and its super classes.
     * This then behaves as expected if a page object extends something which
     * itself extends HtmlElement or BasePage.
     */
    private List<Field> getDeclaredFieldsIncludingSuperClasses(Class<?> childClazz) {
        final List<Field> fields = new ArrayList<>();

        Class<?> parentClazz = childClazz;
        while (parentClazz != null
                && parentClazz != BasePage.class
                && parentClazz != HtmlElement.class) {
            fields.addAll(Arrays.asList(parentClazz.getDeclaredFields()));
            parentClazz = parentClazz.getSuperclass();
        }
        return fields;
    }

    private boolean hasOnlyOneVisibilityAnnotation(Field field) {
        long annotationCount = visibilityAnnotationsOf(field).count();

        if (annotationCount > 1) {
            throw new IllegalArgumentException(String.format(
                    "Field %s on %s has too many Visibility related Annotations",
                    field.getName(),
                    field.getDeclaringClass().getName()));
        } else {
            return annotationCount == 1;
        }
    }

    private Stream<Class<? extends Annotation>> visibilityAnnotationsOf(Field field) {
        return VISIBILITY_ANNOTATION_CLASSES.stream()
                .filter(field::isAnnotationPresent);
    }

    private void invokeWaitFunctionForField(Field field, Object pageObject) {

        Class<? extends Annotation> visibilityAnnotationClass =
                visibilityAnnotationsOf(field)
                        .findAny()
                        .orElseThrow(IllegalStateException::new);

        if (Visible.class.equals(visibilityAnnotationClass)) {
            int toCheckCount = field.getAnnotation(Visible.class).checkAtMost();
            waitForFieldToBeVisible(pageObject, field, toCheckCount);
        } else if (Invisible.class.equals(visibilityAnnotationClass)) {
            int toCheckCount = field.getAnnotation(Invisible.class).checkAtMost();
            waitForFieldToBeInvisible(pageObject, field, toCheckCount);
        } else if (ForceVisible.class.equals(visibilityAnnotationClass)) {
            int toCheckCount = field.getAnnotation(ForceVisible.class).checkAtMost();
            forceThenWaitForFieldToBeVisible(pageObject, field, toCheckCount);
        }
    }

    /**
     * Checks for visibility of Fields with the {@link Visible} annotation.
     * Will recurse inside {@link HtmlElement}s
     *
     * @param pageObject  the pageObject
     * @param field       wait for visibility of the field
     * @param checkAtMost maximum number of elements to check in a List
     */
    @SuppressWarnings("unchecked")
    private void waitForFieldToBeVisible(Object pageObject, Field field, int checkAtMost) {

        Object objectFromField = getObjectFromField(pageObject, field);
        applyToWebElements(
                field,
                objectFromField,
                we -> wait.until(visibilityOf(we)),
                list -> wait.until(visibilityOfAllElements(
                        list.stream()
                                .limit(checkAtMost == -1 ? list.size() : checkAtMost)
                                .collect(Collectors.toList()))));

        // recurse inside HtmlElements
        if (isHtmlElementList(field)) {
            ((List<HtmlElement>) objectFromField)
                    .forEach(this::waitForAnnotatedElementVisibility);
        } else if (isHtmlElement(field)) {
            waitForAnnotatedElementVisibility(objectFromField);
        }
    }

    /**
     * Same as {@link Visibility#waitForFieldToBeVisible(Object, Field, int)}
     * but for Invisibility.
     */
    private void waitForFieldToBeInvisible(Object pageObject, Field field, int checkAtMost) {

        applyToWebElements(
                field,
                getObjectFromField(pageObject, field),
                we -> wait.until(ExtraExpectedConditions.notPresentOrInvisible(we)),
                list -> wait.until(ExtraExpectedConditions.notPresentOrInvisible(
                        list.stream()
                                .limit(checkAtMost == -1 ? list.size() : checkAtMost)
                                .collect(Collectors.toList()))));
    }

    /**
     * Calls {@link Visibility#forceVisible(WebElement)} for each field
     * annotated ith {@code @ForceVisible}, then calls
     * {@link Visibility#waitForFieldToBeVisible(Object, Field, int)}.
     */
    private void forceThenWaitForFieldToBeVisible(Object pageObject, Field field, int checkAtMost) {

        applyToWebElements(
                field,
                getObjectFromField(pageObject, field),
                this::forceVisible,
                list -> list.stream()
                        .limit(checkAtMost == -1 ? list.size() : checkAtMost)
                        .forEach(this::forceVisible));

        waitForFieldToBeVisible(pageObject, field, checkAtMost);
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
            throw new IllegalStateException(e);
        }
    }

    /**
     * Executes JavaScript in an attempt to make the element visible
     * e.g. for elements which are occluded but are required for interaction.
     * To apply this to a list of WebElements, try the following code:
     * {@code elements.forEach(visibility::forceVisible)}
     *
     * @param element the {@link WebElement} to make visible
     */
    void forceVisible(WebElement element) {
        javascriptExecutor.executeScript(FORCE_VISIBLE_SCRIPT, element);
    }
}
