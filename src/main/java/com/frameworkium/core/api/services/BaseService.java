package com.frameworkium.core.api.services;

import com.frameworkium.core.api.annotations.FindBy;
import com.frameworkium.core.common.reporting.allure.AllureLogger;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

public abstract class BaseService<T extends BaseService<T>> {

    protected final Logger logger = LogManager.getLogger(this);

    protected Response response;
    protected JsonPath jsonPath;

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
    public T get(Response response) {

        this.response = response;
        jsonPath = response.jsonPath();
        setFieldsBasedUponAnnotations();

        try {
            AllureLogger.logToAllure("Service '" + this.getClass().getName() + "' successfully loaded");
        } catch (Exception e) {
            logger.error("Error logging page load, but loaded successfully");
        }
        return (T) this;
    }

    private void setFieldsBasedUponAnnotations() {
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            FindBy[] annotations = field.getAnnotationsByType(FindBy.class);
            if (annotations.length == 1) {
                String jp = annotations[0].jsonPath();
                Object value = jsonPath.get(jp);
                if (value != null) {
                    try {
                        field.setAccessible(true);
                        field.set(this, value);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

}