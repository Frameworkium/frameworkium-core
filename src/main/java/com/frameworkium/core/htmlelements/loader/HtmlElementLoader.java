package com.frameworkium.core.htmlelements.loader;


import static com.frameworkium.core.htmlelements.loader.decorator.ProxyFactory.createWebElementProxy;
import static com.frameworkium.core.htmlelements.utils.HtmlElementUtils.getElementName;
import static com.frameworkium.core.htmlelements.utils.HtmlElementUtils.newInstance;

import com.frameworkium.core.htmlelements.element.HtmlElement;
import com.frameworkium.core.htmlelements.element.TypifiedElement;
import com.frameworkium.core.htmlelements.exceptions.HtmlElementsException;
import com.frameworkium.core.htmlelements.loader.decorator.HtmlElementDecorator;
import com.frameworkium.core.htmlelements.loader.decorator.HtmlElementLocatorFactory;
import com.frameworkium.core.htmlelements.loader.decorator.proxyhandlers.WebElementNamedProxyHandler;
import com.frameworkium.core.htmlelements.pagefactory.CustomElementLocatorFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ElementLocator;


/**
 * Initialises blocks of elements and page objects.
 */
public class HtmlElementLoader {

  /**
   * Creates an instance of the given class representing a block of elements and initializes its fields
   * with lazy proxies.
   * <p/>
   * Processes annotation of the given class
   * to set the way of locating the block itself and {@link org.openqa.selenium.support.FindBy},
   * {@link org.openqa.selenium.support.FindBys} and {@link org.openqa.selenium.support.FindAll}
   * annotations of its fields for setting the way of locating elements of the block.
   * <p/>
   * Fields to be proxied:
   * <p/>
   * <ul>
   * <li>{@code WebElement}</li>
   * <li>List of {@code WebElements}</li>
   * <li>Block of elements ({@link HtmlElement} and its successors)</li>
   * <li>List of blocks</li>
   * <li>Typified element ({@link com.frameworkium.core.htmlelements.element.TypifiedElement} successors)</li>
   * <li>List of typified elements</li>
   * </ul>
   *
   * @param clazz         A class to be instantiated and initialized.
   * @param searchContext {@code SearchContext} that will be used to look up the elements.
   * @return Initialized instance of the specified class.
   */
  public static <T extends HtmlElement> T createHtmlElement(Class<T> clazz,
                                                            SearchContext searchContext) {
    ElementLocator locator = new HtmlElementLocatorFactory(searchContext).createLocator(clazz);
    String elementName = getElementName(clazz);

    InvocationHandler handler = new WebElementNamedProxyHandler(locator, elementName);
    WebElement elementToWrap = createWebElementProxy(clazz.getClassLoader(), handler);
    return createHtmlElement(clazz, elementToWrap);
  }

  public static <T extends HtmlElement> T createHtmlElement(Class<T> elementClass,
                                                            WebElement elementToWrap) {
    try {
      T instance = newInstance(elementClass);
      instance.setWrappedElement(elementToWrap);
      // Recursively initialize elements of the block
      populatePageObject(instance, elementToWrap);
      return instance;
    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
        | InvocationTargetException e) {
      throw new HtmlElementsException(e);
    }
  }

  public static <T extends TypifiedElement> T createTypifiedElement(Class<T> clazz,
                                                                    SearchContext searchContext) {
    ElementLocator locator = new HtmlElementLocatorFactory(searchContext).createLocator(clazz);
    String elementName = getElementName(clazz);

    InvocationHandler handler = new WebElementNamedProxyHandler(locator, elementName);
    WebElement elementToWrap = createWebElementProxy(clazz.getClassLoader(), handler);

    return createTypifiedElement(clazz, elementToWrap);
  }

  public static <T extends TypifiedElement> T createTypifiedElement(Class<T> elementClass,
                                                                    WebElement elementToWrap) {
    try {
      return newInstance(elementClass, elementToWrap);
    } catch (NoSuchMethodException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException e) {
      throw new HtmlElementsException(e);
    }
  }

  /**
   * Initializes fields of the given page object with lazy proxies.
   * <p/>
   * Processes {@link org.openqa.selenium.support.FindBy},
   * {@link org.openqa.selenium.support.FindBys} and {@link org.openqa.selenium.support.FindAll}
   * annotations of the fields for setting the way of locating them.
   * <p/>
   * Fields to be proxied:
   * <p/>
   * <ul>
   * <li>{@code WebElement}</li>
   * <li>List of {@code WebElements}</li>
   * <li>Block of elements ({@link HtmlElement} and its successors)</li>
   * <li>List of blocks</li>
   * <li>Typified element ({@link com.frameworkium.core.htmlelements.element.TypifiedElement} successors)</li>
   * <li>List of typified elements</li>
   * </ul>
   *
   * @param page          Page object to be initialized.
   * @param searchContext The {@code WebDriver} instance that will be used to look up the elements.
   */
  public static void populatePageObject(Object page, SearchContext searchContext) {
    populatePageObject(page, new HtmlElementLocatorFactory(searchContext));
  }

  /**
   * Initializes fields of the given page object using specified locator factory.
   *
   * @param page           Page object to be initialized.
   * @param locatorFactory Locator factory that will be used to locate elements.
   */
  public static void populatePageObject(Object page, CustomElementLocatorFactory locatorFactory) {
    PageFactory.initElements(new HtmlElementDecorator(locatorFactory), page);
  }
}
