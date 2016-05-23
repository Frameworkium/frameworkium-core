package com.frameworkium.integration.tfl.api.services.journeyPlanner;

import com.frameworkium.core.api.annotations.DeserialiseAs;
import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.integration.tfl.api.entities.JourneyPlanner.ItineraryResult;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.Arrays;

public class JourneyPlannerItineraryResponse
        extends BaseService<JourneyPlannerItineraryResponse> {

    @DeserialiseAs
    private ItineraryResult itineraryResult;

    @Step
    public int getShortestJourneyDuration() {
        assert itineraryResult.journeys.length > 0;
        return Arrays.stream(itineraryResult.journeys)
                .mapToInt(j -> j.duration)
                .min()
                .orElseThrow(IllegalStateException::new);
    }

}
