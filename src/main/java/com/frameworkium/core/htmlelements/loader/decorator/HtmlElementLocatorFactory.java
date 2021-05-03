package com.frameworkium.core.htmlelements.loader.decorator;

import com.frameworkium.core.htmlelements.annotations.Timeout;
import com.frameworkium.core.htmlelements.pagefactory.CustomElementLocatorFactory;
import com.frameworkium.core.htmlelements.utils.HtmlElementUtils;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.pagefactory.AjaxElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocator;

/**
 * A factory for producing locator instances.
 */
public class HtmlElementLocatorFactory implements CustomElementLocatorFactory {

  private final SearchContext searchContext;

  public HtmlElementLocatorFactory(SearchContext searchContext) {
    this.searchContext = searchContext;
  }

  /**
   * Creates locator for the given field. Created locator will process {@link org.openqa.selenium.support.FindBy},
   * {@link org.openqa.selenium.support.FindBy}, {@link org.openqa.selenium.support.FindBys},
   * {@link org.openqa.selenium.support.FindAll} and {@link org.openqa.selenium.support.CacheLookup} annotations.
   *
   * @param field Field for which locator will be created.
   */
  public ElementLocator createLocator(Field field) {
    return new AjaxElementLocator(searchContext, getTimeOut(field),
        new HtmlElementFieldAnnotationsHandler(field));
  }

  /**
   * Creates locator for the given field. Created locator will process {@link org.openqa.selenium.support.FindBy},
   * {@link org.openqa.selenium.support.FindBy}, {@link org.openqa.selenium.support.FindBys},
   * {@link org.openqa.selenium.support.FindAll} and {@link org.openqa.selenium.support.CacheLookup} annotations.
   *
   * @param clazz Class for which locator will be created.
   */
  @SuppressWarnings("rawtypes")
  public ElementLocator createLocator(Class<?> clazz) {
    return new AjaxElementLocator(searchContext, getTimeOut(clazz),
        new HtmlElementClassAnnotationsHandler(clazz));
  }

  public int getTimeOut(Field field) {
    if (field.isAnnotationPresent(Timeout.class)) {
      return field.getAnnotation(Timeout.class).value();
    }
    if (field.getGenericType() instanceof Class) {
      return getTimeOut((Class<?>) field.getGenericType());
    }
    return getTimeOut(
        (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
  }

  public int getTimeOut(Class<?> clazz) {
    try {
      Method method = Timeout.class.getMethod("value");
      do {
        if (clazz.isAnnotationPresent(Timeout.class)) {
          return (Integer) method.invoke(clazz.getAnnotation(Timeout.class));
        }
        clazz = clazz.getSuperclass();
      }
      while (clazz != Object.class && clazz != null);
    } catch (NoSuchMethodException
        | InvocationTargetException
        | IllegalAccessException ignored) {
    }

    return HtmlElementUtils.getImplicitTimeoutInSeconds();
  }
}
