package com.frameworkium.integration.tfl.api.endpoint.journeyplanner;

import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.integration.tfl.api.constant.Endpoint;
import com.frameworkium.integration.tfl.api.dto.JourneyPlanner.DisambiguationResultDto;
import org.apache.http.HttpStatus;

public class DisambiguationEndpoint extends BaseService<DisambiguationResultDto> {

    public DisambiguationResultDto getDisambiguationResult(String from, String to) {
        requestSpecification
                .expect()
                .statusCode(HttpStatus.SC_OK);
        return newInstance(Endpoint.JOURNEY_PLANNER.getUrl(new String[]{ from, to }));
    }

}
