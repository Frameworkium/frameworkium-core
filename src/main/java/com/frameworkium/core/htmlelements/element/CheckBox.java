package com.frameworkium.core.htmlelements.element;

import java.util.Optional;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Represents checkbox control.
 */
public class CheckBox extends TypifiedElement {

  /**
   * Specifies wrapped {@link WebElement}.
   *
   * @param wrappedElement {@code WebElement} to wrap.
   */
  public CheckBox(WebElement wrappedElement) {
    super(wrappedElement);
  }

  /**
   * Finds label corresponding to this checkbox using "following-sibling::label" xpath.
   *
   * @return Optional of the {@code WebElement} representing label
   */
  public Optional<WebElement> getLabel() {
    try {
      return Optional.of(getWrappedElement().findElement(By.xpath("following-sibling::label")));
    } catch (NoSuchElementException e) {
      return Optional.empty();
    }
  }

  /**
   * Finds the text of the checkbox label.
   *
   * @return Optional of the label text
   */
  public Optional<String> getLabelText() {
    return getLabel().map(WebElement::getText);
  }

  /**
   * The same as {@link #getLabelText()}.
   *
   * @return Text of the checkbox label or {@code null} if no label has been found.
   */
  public String getText() {
    return getLabelText().orElse("");
  }

  /**
   * Selects checkbox if it is not already selected.
   */
  public void select() {
    if (!isSelected()) {
      getWrappedElement().click();
    }
  }

  /**
   * Deselects checkbox if it is not already deselected.
   */
  public void deselect() {
    if (isSelected()) {
      getWrappedElement().click();
    }
  }

  /**
   * Selects checkbox if passed value is {@code true} and deselects otherwise.
   */
  public void set(boolean value) {
    if (value) {
      select();
    } else {
      deselect();
    }
  }
}
