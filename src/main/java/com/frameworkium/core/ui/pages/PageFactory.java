package com.frameworkium.core.ui.pages;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PageFactory {

  private static final Logger logger = LogManager.getLogger();

  protected PageFactory() {
  }

  public static <T extends BasePage<T>> T newInstance(Class<T> clazz) {
    return instantiatePageObject(clazz).get();
  }

  public static <T extends BasePage<T>> T newInstance(
      Class<T> clazz, Duration timeout) {
    return instantiatePageObject(clazz).get(timeout);
  }

  public static <T extends BasePage<T>> T newInstance(
      Class<T> clazz, String url) {
    return instantiatePageObject(clazz).get(url);
  }

  public static <T extends BasePage<T>> T newInstance(
      Class<T> clazz, String url, Duration timeout) {
    return instantiatePageObject(clazz).get(url, timeout);
  }

  private static <T extends BasePage<T>> T instantiatePageObject(Class<T> clazz) {
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException
        | NoSuchMethodException | InvocationTargetException e) {
      logger.fatal("Unable to instantiate PageObject", e);
      throw new IllegalStateException("Unable to instantiate PageObject", e);
    }
  }
}
