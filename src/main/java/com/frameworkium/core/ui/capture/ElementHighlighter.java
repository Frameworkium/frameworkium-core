package com.frameworkium.core.ui.capture;

import org.openqa.selenium.*;

public class ElementHighlighter {

    private JavascriptExecutor js;
    private WebElement previousElem;

    public ElementHighlighter(WebDriver driver) {
        js = (JavascriptExecutor) driver;
    }

    /**
     * Highlight a WebElement.
     *
     * @param webElement to highlight
     */
    public void highlightElement(WebElement webElement) {

        previousElem = webElement; // remember the new element
        try {
            // TODO: save the previous border
            js.executeScript("arguments[0].style.border='3px solid red'", webElement);
        } catch (StaleElementReferenceException ignored) {
            // something went wrong, but no need to crash for highlighting
        }
    }

    /**
     * Unhighlight the previously highlighted WebElement.
     */
    public void unhighlightPrevious() {

        try {
            // unhighlight the previously highlighted element
            js.executeScript("arguments[0].style.border='none'", previousElem);
        } catch (StaleElementReferenceException ignored) {
            // the page was reloaded/changed, the same element isn't there
        }
    }
}
