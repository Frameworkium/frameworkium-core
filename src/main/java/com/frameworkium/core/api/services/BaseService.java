package com.frameworkium.core.api.services;

import com.frameworkium.core.api.annotations.FindBy;
import com.frameworkium.core.common.reporting.allure.AllureLogger;
import com.frameworkium.core.ui.annotations.Visible;
import com.jayway.restassured.RestAssured;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public abstract class BaseService<T extends BaseService<T>> {

    protected final Logger logger = LogManager.getLogger(this);

    /**
     * @return Returns the current page object.
     * Useful for e.g. MyPage.get().then().doSomething();
     */
    @SuppressWarnings("unchecked")
    public T then() {
        return (T) this;
    }

    /**
     * @return Returns the current page object.
     * Useful for e.g. MyPage.get().then().with().aComponent().clickHome();
     */
    @SuppressWarnings("unchecked")
    public T with() {
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T get(String url) {
        RestAssured.get(url);
        return get();
    }

    @SuppressWarnings("unchecked")
    public T get() {

        System.out.println("HERE!!!!!");
        populateFindBys(this);

        try{
            AllureLogger.logToAllure("Service '" + this.getClass().getName() + "' successfully loaded");
        } catch (Exception e) {
            logger.error("Error logging page load, but loaded successfully");
        }
        return (T) this;
    }

    private void populateFindBys(Object serviceObject){
        for (Field field : serviceObject.getClass().getDeclaredFields()) {
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation instanceof FindBy) {

                    Object a = ((FindBy) annotation).jsonPath();
                    try {
                        field.setAccessible(true);
                        field.set(serviceObject, a);
                        field.setAccessible(false);

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }


                }
            }
        }
    }

}