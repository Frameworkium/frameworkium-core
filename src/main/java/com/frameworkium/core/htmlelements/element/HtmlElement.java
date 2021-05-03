package com.frameworkium.core.htmlelements.element;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;

/**
 * The base class to be used for making blocks of elements.
 * <p/>
 * To make a class that will represent a block of elements (e.g. web form)
 * create a descendant of this class and fill it with elements.
 * <p/>
 * For example:
 * <p/>
 * <pre class="code">
 * &#64;FindBy(css = "form_css")
 * public class SearchForm extends HtmlElement {
 *
 *   &#64;FindBy(css = "request_input_css")
 *   private TextInput requestInput;
 * <p/>
 *   &#64;FindBy(css = "search_button_css")
 *   private Button searchButton;
 * <p/>
 *   public TextInput getRequestInput() {
 *   return requestInput;
 *   }
 * <p/>
 *   public Button getSearchButton() {
 *   return searchButton;
 *   }
 * }
 * </pre>
 * <p/>
 * Then you can use created blocks as fields of page objects or you can also
 * initialize them directly with methods of
 * {@link com.frameworkium.core.htmlelements.loader.HtmlElementLoader} class.
 * <p/>
 * Note that this class implements {@link WebElement} interface so you can
 * substitute instances of your block classes for
 * {@code WebElements} where necessary.
 */
public class HtmlElement implements WebElement, WrapsElement {
  private WebElement wrappedElement;

  @Override
  public WebElement getWrappedElement() {
    return wrappedElement;
  }

  /**
   * Sets the wrapped {@code WebElement}. This method is used by
   * initialization mechanism and is not intended to be used directly.
   *
   * @param wrappedElement {@code WebElement} to wrap.
   */
  public void setWrappedElement(WebElement wrappedElement) {
    this.wrappedElement = wrappedElement;
  }

  /**
   * Determines whether or not this element exists on page.
   *
   * @return True if the element exists on page, false otherwise.
   */
  public boolean exists() {
    try {
      getWrappedElement().isDisplayed();
    } catch (NoSuchElementException ignored) {
      return false;
    }
    return true;
  }

  @Override
  public void click() {
    wrappedElement.click();
  }

  /**
   * If this element is a form, or an element within a form, then this will
   * submit this form to the remote server.
   *
   * @see WebElement#submit()
   */
  @Override
  public void submit() {
    wrappedElement.submit();
  }

  @Override
  public void sendKeys(CharSequence... charSequences) {
    wrappedElement.sendKeys(charSequences);
  }

  @Override
  public void clear() {
    wrappedElement.clear();
  }

  @Override
  public String getTagName() {
    return wrappedElement.getTagName();
  }

  @Override
  public String getAttribute(String name) {
    return wrappedElement.getAttribute(name);
  }

  @Override
  public boolean isSelected() {
    return wrappedElement.isSelected();
  }

  @Override
  public boolean isEnabled() {
    return wrappedElement.isEnabled();
  }

  @Override
  public String getText() {
    return wrappedElement.getText();
  }

  @Override
  public List<WebElement> findElements(By by) {
    return wrappedElement.findElements(by);
  }

  @Override
  public WebElement findElement(By by) {
    return wrappedElement.findElement(by);
  }

  @Override
  public boolean isDisplayed() {
    return wrappedElement.isDisplayed();
  }

  @Override
  public Point getLocation() {
    return wrappedElement.getLocation();
  }

  @Override
  public Dimension getSize() {
    return wrappedElement.getSize();
  }

  @Override
  public Rectangle getRect() {
    return wrappedElement.getRect();
  }

  @Override
  public String getCssValue(String name) {
    return wrappedElement.getCssValue(name);
  }

  @Override
  public <X> X getScreenshotAs(OutputType<X> outputType) throws WebDriverException {
    return wrappedElement.getScreenshotAs(outputType);
  }
}
