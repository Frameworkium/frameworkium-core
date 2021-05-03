package com.frameworkium.core.ui.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Invisible {

  /**
   * Default value.
   */
  String value() default "";

  /**
   * If checking for invisibility of a list of elements, setting a value
   * will only check for invisibility of the first n elements of the list.
   */
  int checkAtMost() default -1;
}

