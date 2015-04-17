package com.frameworkium.capture;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ElementHighlighter {
    private JavascriptExecutor js;
    private WebElement lastElem = null;

    public ElementHighlighter(WebDriver driver) {
        js = (JavascriptExecutor) driver;
    }

    public void highlightElement(WebElement elem) {
        // remember the new element
        lastElem = elem;
        js.executeScript("arguments[0].style.border='3px solid red'", elem);
    }

    public void unhighlightLast() {
        try {
            // if there already is a highlighted element, unhighlight it
            js.executeScript("arguments[0].style.border='none'", lastElem);
        } catch (StaleElementReferenceException ignored) {
            // the page got reloaded, the element isn't there
        }
    }
}
