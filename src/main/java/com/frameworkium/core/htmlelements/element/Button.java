package com.frameworkium.core.htmlelements.element;

import org.openqa.selenium.WebElement;

/** Represents web page button control. */
public class Button extends TypifiedElement {

    /**
     * Specifies wrapped {@link WebElement}.
     *
     * @param wrappedElement {@code WebElement} to wrap.
     */
    public Button(WebElement wrappedElement) {
        super(wrappedElement);
    }
}
