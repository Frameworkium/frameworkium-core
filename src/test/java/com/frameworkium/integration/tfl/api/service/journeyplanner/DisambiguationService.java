package com.frameworkium.integration.tfl.api.service.journeyplanner;

import com.frameworkium.integration.tfl.api.dto.journeyplanner.DisambiguationResult;
import com.frameworkium.integration.tfl.api.service.BaseTFLService;
import io.restassured.RestAssured;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

import static com.frameworkium.integration.tfl.api.constant.Endpoint.JOURNEY_PLANNER;

public class DisambiguationService extends BaseTFLService {

    public DisambiguationResult getDisambiguationResult(String from, String to) {
        return performRequest(JOURNEY_PLANNER.getUrl(from, to))
                .as(DisambiguationResult.class);
    }

    /**
     * Overrides {@link BaseTFLService#getResponseSpec()} for this service,
     * which has a different expectation.
     */
    @Override
    protected ResponseSpecification getResponseSpec() {
        return RestAssured
                .expect()
                .statusCode(HttpStatus.SC_MULTIPLE_CHOICES);
    }

}
