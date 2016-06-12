package com.frameworkium.integration.tfl.api.service.journeyplanner;

import com.frameworkium.integration.tfl.api.dto.journeyplanner.Itinerary;
import com.frameworkium.integration.tfl.api.service.BaseTFLService;
import com.google.common.collect.ImmutableMap;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.Map;

import static com.frameworkium.integration.tfl.api.constant.Endpoint.JOURNEY_PLANNER;

public class ItineraryService extends BaseTFLService {

    @Step("Get itinerary from {0} to {1}")
    public Itinerary getItinerary(String from, String to) {
        return request(JOURNEY_PLANNER.getUrl(from, to))
                .as(Itinerary.class);
    }

    @Step("Get national itinerary from {0} to {1}")
    public Itinerary getNationalItinerary(String from, String to) {
        Map<String, String> params = ImmutableMap.of("nationalSearch", "true");
        return request(params, JOURNEY_PLANNER.getUrl(from, to))
                .as(Itinerary.class);
    }
}
