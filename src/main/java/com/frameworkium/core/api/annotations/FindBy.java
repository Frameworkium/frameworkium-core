package com.frameworkium.core.api.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface FindBy {

    String jsonPath() default "";
}
