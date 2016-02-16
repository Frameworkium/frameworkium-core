package com.frameworkium.ui.capture;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ElementHighlighter {
    private JavascriptExecutor js;
    private WebElement previousElem;

    public ElementHighlighter(WebDriver driver) {
        js = (JavascriptExecutor) driver;
    }

    public void highlightElement(WebElement elem) {

        previousElem = elem; // remember the new element
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
