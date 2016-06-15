package com.frameworkium.core.ui;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.yandex.qatools.htmlelements.element.TypifiedElement;

import java.util.List;

/** Frameworkium extension of {@link ExpectedConditions}. */
public class AwaitedConditions {

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
     * Overloaded {@link AwaitedConditions#notPresentOrInvisible(WebElement)}
     * for {@link List} of {@link WebElement}s.
     *
     * @param elements the lazy proxy for <code>List&lt;WebElement&gt;</code> to wait for
     * @return an {@link ExpectedCondition} which returns <strong>false</strong>
     * iff any element is visible, otherwise <strong>true</strong>.
     * @see AwaitedConditions#notPresentOrInvisible(WebElement)
     */
    public static ExpectedCondition<List<? extends WebElement>> notPresentOrInvisible(
            List<? extends WebElement> elements) {

        return driver ->
                elements.stream()
                        .noneMatch(WebElement::isDisplayed)
                        ? elements
                        : null;
    }

    /**
     * @return true iff jQuery is available and 0 ajax queries are active.
     */
    public static ExpectedCondition<Boolean> jQueryAjaxDone() {

        return driver ->
                (Boolean) ((JavascriptExecutor) driver)
                        .executeScript(
                                "return !!window.jQuery && jQuery.active === 0;");
    }

    /**
     * Useful for waiting for items to be added to a list.
     *
     * @param list the lazy proxy for <code>List&lt;WebElement&gt;</code>
     * @param expectedSize expected expectedSize to be greater than
     * @return the original list if list size is greater than expectedSize, else null
     */
    public static ExpectedCondition<List<? extends WebElement>> sizeGreaterThan(
            List<? extends WebElement> list, int expectedSize) {

        return driver ->
                list.size() > expectedSize
                        ? list
                        : null;
    }

    /**
     * Useful for waiting for items to be removed from a list.
     *
     * @param list the lazy proxy for <code>List&lt;WebElement&gt;</code>
     * @param expectedSize expected expectedSize to be less than
     * @return the original list if list size is less than expectedSize, else null
     */
    public static ExpectedCondition<List<? extends WebElement>> sizeLessThan(
            List<? extends WebElement> list, int expectedSize) {

        return driver ->
                list.size() < expectedSize
                        ? list
                        : null;
    }

}
