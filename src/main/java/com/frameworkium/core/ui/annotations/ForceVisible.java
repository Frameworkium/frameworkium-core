package com.frameworkium.core.ui.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForceVisible {
    String value() default "";
}

