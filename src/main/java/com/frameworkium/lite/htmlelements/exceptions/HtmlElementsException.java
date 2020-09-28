package com.frameworkium.lite.htmlelements.exceptions;

/**
 * Thrown during runtime in cases when a block of elements or a
 * page object can't be instantiated or initialized.
 */
public class HtmlElementsException extends RuntimeException {
    public HtmlElementsException() {
        super();
    }

    public HtmlElementsException(String message) {
        super(message);
    }

    public HtmlElementsException(String message, Throwable cause) {
        super(message, cause);
    }

    public HtmlElementsException(Throwable cause) {
        super(cause);
    }
}
