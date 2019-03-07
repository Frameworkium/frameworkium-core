package com.frameworkium.core.ui.litelements.utils;

import com.frameworkium.core.ui.litelements.element.LitElement;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import static ru.yandex.qatools.htmlelements.utils.HtmlElementUtils.getGenericParameterClass;

public class LitElementUtils {

    private LitElementUtils() {
    }

    public static boolean isLitElement(Field field) {
        return isLitElement(field.getType());
    }

    public static boolean isLitElement(Class<?> clazz) {
        return LitElement.class.isAssignableFrom(clazz);
    }

    public static <T> boolean isLitElement(T instance) {
        return LitElement.class.isInstance(instance);
    }

    public static boolean isLitElementList(Field field) {
        if (!isParametrizedList(field)) {
            return false;
        }
        Class listParameterClass = getGenericParameterClass(field);
        return isLitElement(listParameterClass);
    }

    private static boolean isParametrizedList(Field field) {
        return isList(field) && hasGenericParameter(field);
    }

    private static boolean isList(Field field) {
        return List.class.isAssignableFrom(field.getType());
    }

    private static boolean hasGenericParameter(Field field) {
        return field.getGenericType() instanceof ParameterizedType;
    }
}
