package com.frameworkium.ui.pages.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang.StringUtils;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Visible {
    String value() default StringUtils.EMPTY;
}

