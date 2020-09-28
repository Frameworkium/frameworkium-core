package com.frameworkium.lite.ui.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForceVisible {

    /** Default value. */
    String value() default "";

    /**
     * If checking for visibility of a list of elements, setting a value
     * will only check for visibility of the first n elements of a list.
     */
    int checkAtMost() default -1;
}

