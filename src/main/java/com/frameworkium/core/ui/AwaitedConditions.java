package com.frameworkium.core.ui;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.List;

public class AwaitedConditions {

    /**
     * Custom wait which fills the gap left by Selenium whereby
     * <code>not({@link org.openqa.selenium.support.ui.ExpectedConditions#visibilityOf(WebElement)})</code>
     * will fail if the element is not present, but
     * {@link org.openqa.selenium.support.ui.ExpectedConditions#invisibilityOfElementLocated(By)}
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
     * @param elements the list of {@link WebElement}s to wait for
     * @return an {@link ExpectedCondition} which returns <strong>false</strong>
     * iff any element is visible, otherwise <strong>true</strong>.
     * @see AwaitedConditions#notPresentOrInvisible(WebElement)
     */
    public static ExpectedCondition<List<WebElement>> notPresentOrInvisible(
            List<WebElement> elements) {

        return driver ->
                elements.stream()
                        .noneMatch(WebElement::isDisplayed)
                        ? elements
                        : null;
    }

}
