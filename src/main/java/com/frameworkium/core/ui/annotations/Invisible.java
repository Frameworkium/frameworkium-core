package com.frameworkium.core.ui.annotations;

import org.apache.commons.lang.StringUtils;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Invisible {
    String value() default StringUtils.EMPTY;
}

