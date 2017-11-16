package com.frameworkium.core.ui.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Invisible {

    /** Default value. */
    String value() default "";

    /**
     * If checking for invisibility of a list of elements, setting a value
     * will only check for invisibility of the first n elements of the list.
     */
    public int atLeast() default -1;
}

