package com.frameworkium.core.ui.litelements.loader;

import com.frameworkium.core.ui.litelements.element.LitElement;
import com.frameworkium.core.ui.litelements.loader.decorator.LitElementDecorator;
import com.frameworkium.core.ui.litelements.loader.decorator.proxyhandlers.LitElementNamedProxyHandler;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.TypifiedElement;
import ru.yandex.qatools.htmlelements.exceptions.HtmlElementsException;
import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;
import ru.yandex.qatools.htmlelements.loader.decorator.proxyhandlers.WebElementNamedProxyHandler;
import ru.yandex.qatools.htmlelements.pagefactory.CustomElementLocatorFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;

import static com.frameworkium.core.ui.litelements.utils.LitElementUtils.isLitElement;
import static ru.yandex.qatools.htmlelements.loader.decorator.ProxyFactory.createWebElementProxy;
import static ru.yandex.qatools.htmlelements.utils.HtmlElementUtils.*;

public class LitElementLoader extends HtmlElementLoader {

    /**
     * Creates and initializes a block of elements if the given class is {@link HtmlElement},{@link LitElement}
     * or its successor and initializes page object otherwise.
     *
     * @param clazz  A class to be instantiated and initialized.
     * @param driver The {@code WebDriver} instance that will be used to look up the elements.
     * @return Initialized instance of the specified class.
     * @see #createLitElement(Class, SearchContext)
     * @see #createHtmlElement(Class, SearchContext)
     * @see #createPageObject(Class, WebDriver)
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> clazz, WebDriver driver) {
        if (isLitElement(clazz)) {
            return (T) createLitElement((Class<LitElement>) clazz, driver);
        }
        if (isHtmlElement(clazz)) {
            return (T) createHtmlElement((Class<HtmlElement>) clazz, driver);
        }
        if (isTypifiedElement(clazz)) {
            return (T) createTypifiedElement((Class<TypifiedElement>) clazz, driver);
        }
        return createPageObject(clazz, driver);
    }

    /**
     * Initializes {@code instance} as a block of elements it is instance of {@link HtmlElement},{@link LitElement}
     * or its successor and as a page object otherwise.
     *
     * @param instance Object to be initialized.
     * @param driver   The {@code WebDriver} instance that will be used to look up the elements.
     * @see #populateLitElement(LitElement, SearchContext)
     * @see #populateHtmlElement(HtmlElement, SearchContext)
     * @see #createPageObject(Class, WebDriver)
     */
    public static <T> void populate(T instance, WebDriver driver) {

        if (isLitElement(instance)) {
            populateLitElement((LitElement) instance, driver);
        } else if (isHtmlElement(instance)) {
            populateHtmlElement((HtmlElement) instance, driver);
        } else {
            // Otherwise consider instance as a page object
            populatePageObject(instance, driver);
        }
    }

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
     * <li>List of {@code WebElements}</li>     *
     * <li>Block of elements ({@link HtmlElement} and its successors)</li>
     * <li>List of blocks</li>
     * <li>Typified element ({@link ru.yandex.qatools.htmlelements.element.TypifiedElement} successors)</li>
     * <li>List of typified elements</li>
     * </ul>
     *
     * @param clazz         A class to be instantiated and initialized.
     * @param searchContext {@code SearchContext} that will be used to look up the elements.
     * @return Initialized instance of the specified class.
     */
    public static <T extends HtmlElement> T createHtmlElement(Class<T> clazz, SearchContext searchContext) {
        ElementLocator locator = new HtmlElementLocatorFactory(searchContext).createLocator(clazz);
        String elementName = getElementName(clazz);

        InvocationHandler handler = new WebElementNamedProxyHandler(locator, elementName);
        WebElement elementToWrap = createWebElementProxy(clazz.getClassLoader(), handler);
        return createHtmlElement(clazz, elementToWrap, elementName);
    }

    /**
     * Creates {@code HtmlElement} instance and recursive populate page objects within it
     */
    public static <T extends HtmlElement> T createHtmlElement(Class<T> elementClass, WebElement elementToWrap,
                                                              String name) {
        try {
            T instance = newInstance(elementClass);
            instance.setWrappedElement(elementToWrap);
            instance.setName(name);
            // Recursively initialize elements of the block
            populatePageObject(instance, elementToWrap);
            return instance;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            throw new HtmlElementsException(e);
        }
    }

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
     * <li>Block of elements ({@link LitElement} and its successors)</li>
     * <li>List of blocks</li>
     * <li>Typified element ({@link ru.yandex.qatools.htmlelements.element.TypifiedElement} successors)</li>
     * <li>List of typified elements</li>
     * </ul>
     *
     * @param clazz         A class to be instantiated and initialized.
     * @param searchContext {@code SearchContext} that will be used to look up the elements.
     * @return Initialized instance of the specified class.
     */
    public static <T extends LitElement> T createLitElement(Class<T> clazz, SearchContext searchContext) {
        ElementLocator locator = new HtmlElementLocatorFactory(searchContext).createLocator(clazz);
        String elementName = getElementName(clazz);

        InvocationHandler handler = new LitElementNamedProxyHandler(locator, elementName);
        WebElement elementToWrap = createWebElementProxy(clazz.getClassLoader(), handler);
        return createLitElement(clazz, elementToWrap, elementName);
    }

    /**
     * Creates {@code LitElement} instance and recursive populate page objects within it
     */
    public static <T extends LitElement> T createLitElement(Class<T> elementClass, WebElement elementToWrap,
                                                            String name) {
        try {
            T instance = newInstance(elementClass);
            instance.setWrappedElement(elementToWrap);
            instance.setName(name);
            // Recursively initialize elements of the block
            populatePageObject(instance, elementToWrap);
            return instance;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            throw new HtmlElementsException(e);
        }
    }

    /**
     * Creates an instance of the given page object class and initializes its fields with lazy proxies.
     * <p/>
     * Processes {@link org.openqa.selenium.support.FindBy},
     * {@link org.openqa.selenium.support.FindBys} and {@link org.openqa.selenium.support.FindAll}
     * annotations of the fields for setting the way of locating them.
     * <p/>
     * Fields to be proxied:
     * <p/>
     * <ul>
     * <li>{@code WebElement}</li>
     *
     * <li>List of {@code WebElements}</li>
     * <li>Block of elements ({@link LitElement} and its successors)</li>
     * <li>Block of elements ({@link HtmlElement} and its successors)</li>
     * <li>List of blocks</li>
     * <li>Typified element ({@link ru.yandex.qatools.htmlelements.element.TypifiedElement} successors)</li>
     * <li>List of typified elements</li>
     * </ul>
     *
     * @param clazz  A class to be instantiated and initialized.
     * @param driver The {@code WebDriver} instance that will be used to look up the elements.
     * @return Initialized instance of the specified class.
     */
    public static <T> T createPageObject(Class<T> clazz, WebDriver driver) {
        try {
            T instance = newInstance(clazz, driver);
            populatePageObject(instance, driver);
            return instance;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            throw new HtmlElementsException(e);
        }
    }

    /**
     * Initializes fields of the given block of elements with lazy proxies.
     * <p/>
     * Processes annotation of the class
     * of the given block to set the way of locating the block itself and {@link org.openqa.selenium.support.FindBy},
     * {@link org.openqa.selenium.support.FindBys} and {@link org.openqa.selenium.support.FindAll} annotations of its
     * fields for setting the way of locating elements of the block.
     * <p/>
     * Fields to be proxied:
     * <p/>
     * <ul>
     * <li>{@code WebElement}</li>
     * <li>List of {@code WebElements}</li>     *
     * <li>Block of elements ({@link HtmlElement} and its successors)</li>
     * <li>List of blocks</li>
     * <li>Typified element ({@link ru.yandex.qatools.htmlelements.element.TypifiedElement} successors)</li>
     * <li>List of typified elements</li>
     * </ul>
     *
     * @param htmlElement   Block of elements to be initialized.
     * @param searchContext {@code SearchContext} that will be used to look up the elements.
     */
    public static void populateHtmlElement(HtmlElement htmlElement, SearchContext searchContext) {
        populateHtmlElement(htmlElement, new HtmlElementLocatorFactory(searchContext));
    }

    /**
     * Initializes fields of the given block of elements using specified locator factory.
     *
     * @param htmlElement    Block of elements to be initialized.
     * @param locatorFactory Locator factory that will be used to locate block elements.
     */
    public static void populateHtmlElement(HtmlElement htmlElement, CustomElementLocatorFactory locatorFactory) {
        @SuppressWarnings("unchecked")
        Class<HtmlElement> elementClass = (Class<HtmlElement>) htmlElement.getClass();
        // Create locator that will handle Block annotation
        ElementLocator locator = locatorFactory.createLocator(elementClass);
        ClassLoader elementClassLoader = htmlElement.getClass().getClassLoader();
        // Initialize block with WebElement proxy and set its name
        String elementName = getElementName(elementClass);
        InvocationHandler handler = new WebElementNamedProxyHandler(locator, elementName);
        WebElement elementToWrap = createWebElementProxy(elementClassLoader, handler);
        htmlElement.setWrappedElement(elementToWrap);
        htmlElement.setName(elementName);
        // Initialize elements of the block
        populatePageObject(htmlElement, elementToWrap);
    }

    /**
     * Initializes fields of the given block of elements with lazy proxies.
     * <p/>
     * Processes annotation of the class
     * of the given block to set the way of locating the block itself and {@link org.openqa.selenium.support.FindBy},
     * {@link org.openqa.selenium.support.FindBys} and {@link org.openqa.selenium.support.FindAll} annotations of its
     * fields for setting the way of locating elements of the block.
     * <p/>
     * Fields to be proxied:
     * <p/>
     * <ul>
     * <li>{@code WebElement}</li>
     * <li>List of {@code WebElements}</li>
     * <li>Block of elements ({@link LitElement} and its successors)</li>
     * <li>Block of elements ({@link HtmlElement} and its successors)</li>
     * <li>List of blocks</li>
     * <li>Typified element ({@link ru.yandex.qatools.htmlelements.element.TypifiedElement} successors)</li>
     * <li>List of typified elements</li>
     * </ul>
     *
     * @param litElement    Block of elements to be initialized.
     * @param searchContext {@code SearchContext} that will be used to look up the elements.
     */
    public static void populateLitElement(LitElement litElement, SearchContext searchContext) {
        populateLitElement(litElement, new HtmlElementLocatorFactory(searchContext));
    }

    /**
     * Initializes fields of the given block of elements using specified locator factory.
     *
     * @param litelement     Block of elements to be initialized.
     * @param locatorFactory Locator factory that will be used to locate block elements.
     */
    public static void populateLitElement(LitElement litelement, CustomElementLocatorFactory locatorFactory) {
        @SuppressWarnings("unchecked")
        Class<LitElement> elementClass = (Class<LitElement>) litelement.getClass();
        // Create locator that will handle Block annotation
        ElementLocator locator = locatorFactory.createLocator(elementClass);
        ClassLoader elementClassLoader = litelement.getClass().getClassLoader();
        // Initialize block with WebElement proxy and set its name
        String elementName = getElementName(elementClass);
        InvocationHandler handler = new LitElementNamedProxyHandler(locator, elementName);
        WebElement elementToWrap = createWebElementProxy(elementClassLoader, handler);
        litelement.setWrappedElement(elementToWrap);
        litelement.setName(elementName);
        // Initialize elements of the block
        populatePageObject(litelement, elementToWrap);
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
     * <li>Block of elements ({@link LitElement} and its successors)</li>
     * <li>Block of elements ({@link HtmlElement} and its successors)</li>
     * <li>List of blocks</li>
     * <li>Typified element ({@link ru.yandex.qatools.htmlelements.element.TypifiedElement} successors)</li>
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
        PageFactory.initElements(new LitElementDecorator(locatorFactory), page);
    }
}