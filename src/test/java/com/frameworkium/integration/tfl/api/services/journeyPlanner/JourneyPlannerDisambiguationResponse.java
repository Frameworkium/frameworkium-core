package com.frameworkium.integration.tfl.api.services.journeyPlanner;

import com.frameworkium.core.api.annotations.DeserialiseAs;
import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.integration.tfl.api.entities.JourneyPlanner.DisambiguationResult;
import ru.yandex.qatools.allure.annotations.Step;

public class JourneyPlannerDisambiguationResponse
        extends BaseService<JourneyPlannerDisambiguationResponse> {

    @DeserialiseAs
    private DisambiguationResult result;

    @Step
    public String getFrom() {
        return result.journeyVector.from;
    }

    @Step
    public String getTo() {
        return result.journeyVector.to;
    }

    @Step
    public String getFirstDisambiguatedTo() {
        return result.toLocationDisambiguation.disambiguationOptions[0].parameterValue;
    }

}
