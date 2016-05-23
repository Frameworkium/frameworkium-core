package com.frameworkium.integration.tfl.api.services.journeyPlanner;

import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.core.api.services.ServiceFactory;

import java.util.Collections;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;

public interface JourneyPlannerService {

    static <T extends BaseService<T>> T newInstance(
            Class<T> serviceClass, String from, String to) {
        return newInstance(serviceClass, from, to, Collections.emptyMap());
    }

    static <T extends BaseService<T>> T newInstance(
            Class<T> serviceClass, String from, String to, Map<String, String> params) {

        return ServiceFactory.newInstance(
                serviceClass,
                String.format("http://api.tfl.gov.uk/Journey/JourneyResults/%s/to/%s", from, to),
                given().parameters(params));
    }
}
