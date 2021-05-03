package com.frameworkium.core.htmlelements.element;

import org.openqa.selenium.WebElement;

/**
 * Represents an anchor tag/hyperlink.
 */
public class Link extends TypifiedElement {

  public Link(WebElement wrappedElement) {
    super(wrappedElement);
  }

  /**
   * Retrieves reference from "href" attribute.
   *
   * @return Reference associated with hyperlink.
   */
  public String getReference() {
    return getWrappedElement().getAttribute("href");
  }
}
