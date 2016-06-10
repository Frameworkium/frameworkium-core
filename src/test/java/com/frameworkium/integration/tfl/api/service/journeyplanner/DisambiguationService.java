package com.frameworkium.integration.tfl.api.service.journeyplanner;

import com.frameworkium.integration.tfl.api.constant.Endpoint;
import com.frameworkium.integration.tfl.api.dto.journeyplanner.DisambiguationResult;
import com.frameworkium.integration.tfl.api.service.BaseTFLService;
import com.jayway.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

public class DisambiguationService extends BaseTFLService {

    public DisambiguationResult getDisambiguationResult(String from, String to) {
        return getRequestSpecification()
                .response().spec(getResponseSpecification())
                .get(Endpoint.JOURNEY_PLANNER.getUrl(from, to))
                .as(DisambiguationResult.class);
    }

    protected ResponseSpecification getResponseSpecification() {
        return getRequestSpecification()
                .expect()
                .statusCode(HttpStatus.SC_MULTIPLE_CHOICES);
    }

}
