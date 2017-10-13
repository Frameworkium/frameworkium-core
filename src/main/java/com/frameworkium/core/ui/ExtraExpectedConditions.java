package com.frameworkium.core.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.TypifiedElement;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Frameworkium extension of {@link ExpectedConditions}.
 *
 * <p>The main motivation is to provide useful {@link ExpectedCondition}'s for our
 * lazy proxied {@link WebElement}, {@link TypifiedElement} and {@link HtmlElement}'s.
 * Hence the methods here generally accept <code>? extends WebElement</code>
 * rather than {@link By} because we typically use <code>@FindBy</code> annotations.
 */
public class ExtraExpectedConditions {

    /**
     * Custom wait which fills the gap left by Selenium whereby
     * <code>not({@link ExpectedConditions#visibilityOf(WebElement)})</code>
     * will fail if the element is not present, but
     * {@link ExpectedConditions#invisibilityOfElementLocated(By)}
     * waits for either not visibility or not present.
     *
     * @param element the element to wait for
     * @return an {@link ExpectedCondition} which returns <strong>false</strong>
     *      if the element is visible, otherwise <strong>true</strong>.
     */
    public static ExpectedCondition<Boolean> notPresentOrInvisible(final WebElement element) {

        return expectedCondition(driver -> {
            try {
                return !element.isDisplayed();
            } catch (NoSuchElementException e) {
                return true;
            }
        },
                String.format("element '%s' to not be present or be invisible", element));
    }

    /**
     * Overloaded {@link ExtraExpectedConditions#notPresentOrInvisible(WebElement)}
     * for {@link List} of {@link WebElement}s.
     *
     * @param elements the lazy proxy for <code>List&lt;WebElement&gt;</code> to wait for
     * @return an {@link ExpectedCondition} which returns the <strong>list</strong>
     *      iff any element is visible, otherwise <strong>null</strong>.
     * @see ExtraExpectedConditions#notPresentOrInvisible(WebElement)
     */
    public static ExpectedCondition<List<? extends WebElement>> notPresentOrInvisible(
            final List<? extends WebElement> elements) {

        return expectedCondition(driver ->
                        elements.stream()
                                .noneMatch(WebElement::isDisplayed)
                                ? elements
                                : null,
                String.format(
                        "the following elements to not be present or be invisible: %s",
                        elements.stream()
                                .map(WebElement::toString)
                                .collect(Collectors.joining(", "))));
    }

    /**
     * Useful for waiting for items to be added to a list.
     *
     * @param list         the lazy proxy for <code>List&lt;WebElement&gt;</code>
     * @param expectedSize expected expectedSize to be greater than
     * @return the original list if list size is greater than expectedSize, else null
     */
    public static ExpectedCondition<List<? extends WebElement>> sizeGreaterThan(
            List<? extends WebElement> list, int expectedSize) {

        return expectedCondition(
            driver -> list.size() > expectedSize ? list : null,
            "list size of " + list.size() + " to be greater than " + expectedSize);
    }

    /**
     * Useful for waiting for items to be removed from a list.
     *
     * @param list         the lazy proxy for <code>List&lt;WebElement&gt;</code>
     * @param expectedSize expected expectedSize to be less than
     * @return the original list if list size is less than expectedSize, else null
     */
    public static ExpectedCondition<List<? extends WebElement>> sizeLessThan(
            List<? extends WebElement> list, int expectedSize) {

        return expectedCondition(
            driver -> list.size() < expectedSize ? list : null,
            "list size of " + list.size() + " to be less than " + expectedSize);
    }

    /**
     * Wait until a jQuery is done.
     *
     * @return true iff jQuery is available and 0 ajax queries are active.
     */
    public static ExpectedCondition<Boolean> jQueryAjaxDone() {

        return javascriptExpectedCondition(
                "return !!window.jQuery && jQuery.active === 0;",
                "jQuery AJAX queries to not be active");
    }

    /**
     * Wait for the document ready state to equal 'complete'.
     * Useful for javascript loading on page-load.
     *
     * @return a {@link ExpectedCondition} which returns <strong>false</strong> if the document
     *          isn't ready, and <string>true</string> if the document is ready
     */
    public static ExpectedCondition<Boolean> documentBodyReady() {

        return javascriptExpectedCondition(
                "return document.readyState == 'complete';",
                "the document ready state to equal 'complete'");
    }

    private static ExpectedCondition<Boolean> javascriptExpectedCondition(
            String query, String message) {
        return expectedCondition(
            driver -> (Boolean) ((JavascriptExecutor) driver).executeScript(query),
            message);
    }

    private static <T> ExpectedCondition<T> expectedCondition(
            Function<WebDriver, T> function, String string) {

        return new ExpectedCondition<T>() {
            @Override
            public T apply(WebDriver driver) {
                return function.apply(driver);
            }

            @Override
            public String toString() {
                return string;
            }
        };
    }
}
