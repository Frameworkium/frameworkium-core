package com.frameworkium.integration.tfl.api.endpoint.journeyplanner;

import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.integration.tfl.api.dto.JourneyPlanner.ItineraryDto;
import ru.yandex.qatools.allure.annotations.Step;

import static com.frameworkium.integration.tfl.api.constant.Endpoint.JOURNEY_PLANNER;

public class ItineraryEndpoint extends BaseService<ItineraryDto> {

    @Step
    public ItineraryDto getItinerary(String from, String to) {
        return getItinerary(from, to, false);
    }

    @Step
    public ItineraryDto getItinerary(String from, String to, boolean nationalSearch) {
        if (nationalSearch) {
            requestSpecification
                    .param("nationalSearch", "true");
        }
        return newInstance(JOURNEY_PLANNER.getUrl(new String[]{ from, to }));
    }

}
