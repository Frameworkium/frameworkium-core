package com.frameworkium.core.api.annotations;

import org.apache.commons.lang.StringUtils;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DeserialiseAs {
    String value() default StringUtils.EMPTY;
}
