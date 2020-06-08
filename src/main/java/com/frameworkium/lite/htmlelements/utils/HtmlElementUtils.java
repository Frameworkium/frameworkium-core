package com.frameworkium.lite.htmlelements.utils;

import com.frameworkium.lite.htmlelements.element.HtmlElement;
import com.frameworkium.lite.htmlelements.element.TypifiedElement;
import com.frameworkium.lite.htmlelements.exceptions.HtmlElementsException;
import com.google.common.collect.Lists;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

import java.lang.reflect.*;
import java.net.URL;
import java.util.List;

import static org.apache.commons.lang3.reflect.ConstructorUtils.invokeConstructor;

/** Contains utility methods used in framework. */
public final class HtmlElementUtils {

    /**
     * Default implicit timeout for HtmlElements, should be less than
     * any explicit waits to prevent
     * {@code org.openqa.selenium.TimeoutException: Supplied function might have stalled}
     */
    public static final int DEFAULT_TIMEOUT_SECS = 6;

    private HtmlElementUtils() {
    }

    public static <T> T newInstance(Class<T> clazz, Object... args) throws IllegalAccessException,
            InstantiationException, NoSuchMethodException, InvocationTargetException {
        if (clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers())) {
            Class<?> outerClass = clazz.getDeclaringClass();
            Object outerObject = outerClass.newInstance();
            return invokeConstructor(clazz, Lists.asList(outerObject, args).toArray());
        }
        return invokeConstructor(clazz, args);
    }

    public static boolean isHtmlElement(Field field) {
        return isHtmlElement(field.getType());
    }

    public static boolean isHtmlElement(Class<?> clazz) {
        return HtmlElement.class.isAssignableFrom(clazz);
    }

    public static <T> boolean isHtmlElement(T instance) {
        return instance instanceof HtmlElement;
    }

    public static boolean isTypifiedElement(Field field) {
        return isTypifiedElement(field.getType());
    }

    public static boolean isTypifiedElement(Class<?> clazz) {
        return TypifiedElement.class.isAssignableFrom(clazz);
    }

    public static boolean isWebElement(Field field) {
        return isWebElement(field.getType());
    }

    public static boolean isWebElement(Class<?> clazz) {
        return WebElement.class.isAssignableFrom(clazz);
    }

    public static boolean isHtmlElementList(Field field) {
        if (!isParametrizedList(field)) {
            return false;
        }
        Class<?> listParameterClass = getGenericParameterClass(field);
        return isHtmlElement(listParameterClass);
    }

    public static boolean isTypifiedElementList(Field field) {
        if (!isParametrizedList(field)) {
            return false;
        }
        Class<?> listParameterClass = getGenericParameterClass(field);
        return isTypifiedElement(listParameterClass);
    }

    public static boolean isWebElementList(Field field) {
        if (!isParametrizedList(field)) {
            return false;
        }
        Class<?> listParameterClass = getGenericParameterClass(field);
        return isWebElement(listParameterClass);
    }

    public static Class<?> getGenericParameterClass(Field field) {
        Type genericType = field.getGenericType();
        return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
    }

    private static boolean isParametrizedList(Field field) {
        return isList(field) && hasGenericParameter(field);
    }

    private static boolean isList(Field field) {
        return List.class.isAssignableFrom(field.getType());
    }

    private static boolean hasGenericParameter(Field field) {
        return field.getGenericType() instanceof ParameterizedType;
    }

    public static String getElementName(Field field) {
        return field.getName();
    }

    public static <T> String getElementName(Class<T> clazz) {
        return clazz.getSimpleName();
    }

    public static boolean isRemoteWebElement(WebElement element) {
        return element.getClass().equals(RemoteWebElement.class);
    }

    public static boolean isOnRemoteWebDriver(WebElement element) {
        if (!isRemoteWebElement(element)) {
            return false;
        }

        // Since subclasses of RemoteWebElement were finally removed in Selenium 2.26.0, WebElements on local drivers
        // are also instances of RemoteWebElement class. The only way that we found at the current moment to find out
        // whether WebElement instance is on remote driver is to check the class of RemoteWebElement "parent" filed,
        // which contains WebDriver instance to which this RemoteWebElement belongs.
        // As this field has protected access this is done by reflection.
        // TODO It's is a kind of a dirty hack to be improved in future versions.
        RemoteWebElement remoteWebElement = (RemoteWebElement) element;
        try {
            Field elementParentFiled = RemoteWebElement.class.getDeclaredField("parent");
            elementParentFiled.setAccessible(true);
            WebDriver elementParent = (WebDriver) elementParentFiled.get(remoteWebElement);
            return elementParent.getClass().equals(RemoteWebDriver.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new HtmlElementsException("Unable to find out if WebElement is on remote driver", e);
        }
    }

    public static boolean existsInClasspath(final String fileName) {
        return getResourceFromClasspath(fileName) != null;
    }

    public static URL getResourceFromClasspath(final String fileName) {
        return Thread.currentThread().getContextClassLoader().getResource(fileName);
    }

    public static int getImplicitTimeoutInSeconds() {
        return Integer.getInteger("webdriver.timeouts.implicitlywait", DEFAULT_TIMEOUT_SECS);
    }
}
