package com.frameworkium.core.htmlelements.loader.decorator;

import com.frameworkium.core.htmlelements.element.HtmlElement;
import com.frameworkium.core.htmlelements.exceptions.HtmlElementsException;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBy.FindByBuilder;
import org.openqa.selenium.support.pagefactory.AbstractAnnotations;

/**
 * Handles annotation of {@link HtmlElement} and its successors.
 */
public class HtmlElementClassAnnotationsHandler<T extends HtmlElement> extends AbstractAnnotations {

  private final Class<T> elementClass;

  public HtmlElementClassAnnotationsHandler(Class<T> elementClass) {
    this.elementClass = elementClass;
  }

  @Override
  public By buildBy() {
    Class<?> clazz = elementClass;
    while (clazz != Object.class) {
      if (clazz.isAnnotationPresent(FindBy.class)) {
        return new FindByBuilder().buildIt(clazz.getAnnotation(FindBy.class), null);
      }
      clazz = clazz.getSuperclass();
    }

    throw new HtmlElementsException(String.format(
        "Cannot determine how to locate instance of %s", elementClass));
  }

  @Override
  public boolean isLookupCached() {
    return false;
  }
}
