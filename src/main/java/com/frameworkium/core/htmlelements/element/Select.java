package com.frameworkium.core.htmlelements.element;

import java.util.List;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ISelect;

/**
 * Represents a select control.
 * <p/>
 * This class wraps the Selenium {@link org.openqa.selenium.support.ui.Select}
 * and delegates all method calls to it.
 * <p>
 * But unlike {@code WebDriver} {@code Select} class there are no checks
 * performed in the constructor of this class, so it can be used correctly
 * with lazy initialization.
 */
public class Select extends TypifiedElement implements ISelect {

  /**
   * Specifies wrapped {@link WebElement}.
   * Performs no checks unlike {@link org.openqa.selenium.support.ui.Select}.
   * All checks are made later in {@link #getSelect()} method.
   *
   * @param wrappedElement {@code WebElement} to wrap.
   */
  public Select(WebElement wrappedElement) {
    super(wrappedElement);
  }

  /**
   * Constructs instance of {@link org.openqa.selenium.support.ui.Select} class.
   *
   * @return {@link org.openqa.selenium.support.ui.Select} class instance.
   */
  private org.openqa.selenium.support.ui.Select getSelect() {
    return new org.openqa.selenium.support.ui.Select(getWrappedElement());
  }

  public boolean isMultiple() {
    return getSelect().isMultiple();
  }

  public List<WebElement> getOptions() {
    return getSelect().getOptions();
  }

  public List<WebElement> getAllSelectedOptions() {
    return getSelect().getAllSelectedOptions();
  }

  public WebElement getFirstSelectedOption() {
    return getSelect().getFirstSelectedOption();
  }

  /**
   * Indicates if select has at least one selected option.
   *
   * @return {@code true} if select has at least one selected option and
   *     {@code false} otherwise.
   */
  public boolean hasSelectedOption() {
    return getOptions().stream().anyMatch(WebElement::isSelected);
  }

  public void selectByVisibleText(String text) {
    getSelect().selectByVisibleText(text);
  }

  public void selectByIndex(int index) {
    getSelect().selectByIndex(index);
  }

  public void selectByValue(String value) {
    getSelect().selectByValue(value);
  }

  public void deselectAll() {
    getSelect().deselectAll();
  }

  public void deselectByValue(String value) {
    getSelect().deselectByValue(value);
  }

  public void deselectByIndex(int index) {
    getSelect().deselectByIndex(index);
  }

  public void deselectByVisibleText(String text) {
    getSelect().deselectByVisibleText(text);
  }
}
