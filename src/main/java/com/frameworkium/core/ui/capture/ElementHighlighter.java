package com.frameworkium.core.ui.capture;

import org.openqa.selenium.*;

public class ElementHighlighter {

    private JavascriptExecutor js;
    private WebElement previousElem;

    public ElementHighlighter(WebDriver driver) {
        js = (JavascriptExecutor) driver;
    }

    public void highlightElement(WebElement elem) {

        previousElem = elem; // remember the new element
        // TODO: save the previous border
        js.executeScript("arguments[0].style.border='3px solid red'", elem);
    }

    public void unhighlightPrevious() {

        try {
            // unhighlight the previously highlighted element
            js.executeScript("arguments[0].style.border='none'", previousElem);
        } catch (StaleElementReferenceException ignored) {
            // the page was reloaded, the element isn't there
        }
    }
}
