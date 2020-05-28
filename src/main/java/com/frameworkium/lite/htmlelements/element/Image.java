package com.frameworkium.lite.htmlelements.element;

import org.openqa.selenium.WebElement;

/** Represents an image {@code <img />} */
public class Image extends TypifiedElement {

    public Image(WebElement wrappedElement) {
        super(wrappedElement);
    }

    /**
     * Retrieves path to image from "src" attribute
     *
     * @return Path to the image
     */
    public String getSource() {
        return getWrappedElement().getAttribute("src");
    }

    /**
     * Retrieves alternative text from "alt" attribute
     *
     * @return alternative text for image
     */
    public String getAlt() {
        return getWrappedElement().getAttribute("alt");
    }

}
