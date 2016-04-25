package com.frameworkium.core.api.services;

import com.frameworkium.core.api.annotations.FindBy;
import com.frameworkium.core.common.reporting.allure.AllureLogger;
import com.jayway.restassured.http.Method;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.Method.*;

public abstract class BaseService<T extends BaseService<T>> {

    protected final Logger logger = LogManager.getLogger(this);

    protected Response response;
    protected JsonPath jsonPath;

    protected String url;
    protected Method httpVerb;
    protected RequestSpecification reqSpec;

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
        return get(url, Method.GET, given());
    }

    @SuppressWarnings("unchecked")
    public T get(String url, RequestSpecification requestSpec) {
        return get(url, Method.GET, requestSpec);
    }

    @SuppressWarnings("unchecked")
    public T get(String url, Method httpVerb, RequestSpecification requestSpec) {

        //Store these in case you want to re-fire the request unmodified without reinstantiating
        this.url = url;
        this.httpVerb = httpVerb;
        this.reqSpec = requestSpec;

        //make the request
        this.response = makeRequest(httpVerb, url, requestSpec);
        jsonPath = response.jsonPath();
        setFieldsBasedUponAnnotations();

        try {
            AllureLogger.logToAllure("Service '" + this.getClass().getName() + "' successfully loaded");
        } catch (Exception e) {
            logger.error("Error logging page load, but loaded successfully");
        }
        return (T) this;
    }

    private Response makeRequest(Method httpVerb, String url, RequestSpecification requestSpec) {

        //TODO - enable automatic logging to the logger - eg errors to error, rest to debug?
        //requestSpec = requestSpec.config(RestAssured.config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails(HEADERS)));

        Response resp = null;

        logger.info("Making request: " + httpVerb.toString() + " " + url);
        //requestSpec = requestSpec.log().all();

        if(httpVerb.equals(GET))
            resp = requestSpec.get(url);
        else if (httpVerb.equals(POST))
            resp = requestSpec.post(url);
        else if(httpVerb.equals(PUT))
            resp = requestSpec.put(url);
        else if(httpVerb.equals(DELETE))
            resp = requestSpec.delete(url);
        else if(httpVerb.equals(Method.HEAD))
            resp = requestSpec.head(url);
        else if(httpVerb.equals(Method.OPTIONS))
            resp = requestSpec.options(url);
        else if(httpVerb.equals(Method.PATCH))
            resp = requestSpec.patch(url);
        else {
            logger.error("Unrecognised http verb supplied - defaulting to a get");
            resp = requestSpec.get(url);
        }

        return resp;
    }


    private void setFieldsBasedUponAnnotations() {
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            FindBy[] annotations = field.getAnnotationsByType(FindBy.class);
            if (annotations.length == 1) {
                String jp = annotations[0].jsonPath();

                //Force to get a list of the specified type
                // if a list is specified
                Class fieldClass = field.getType();
                Object value = null;
                if(fieldClass.equals(List.class)){
                    ParameterizedType listType = (ParameterizedType) field.getGenericType();
                    Class fieldSubClass = (Class) listType.getActualTypeArguments()[0];
                    value = jsonPath.getList(jp, fieldSubClass);
                } else {
                    value = jsonPath.get(jp);
                }

                //If we only requested one thing, but actually found many,
                // only return first instance found
                if((value instanceof Collection) && !fieldClass.equals(List.class)){
                    value = ((List) value).get(0);
                }

                //If we wanted the data as a String, let's try to make it one
                if(!(value instanceof String) && fieldClass.equals(String.class)){
                    value = String.valueOf(value);
                }


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

    public String prettyPrintResponse(){
        return this.response.prettyPrint();
    }

    public void resendLastRequest(){
        get(url,httpVerb,reqSpec);
    }

    public int getResponseCode(){
        return response.getStatusCode();
    }

}