package com.frameworkium.integration.tfl.api.service.journeyplanner;

import com.frameworkium.integration.tfl.api.dto.journeyplanner.Itinerary;
import com.frameworkium.integration.tfl.api.service.BaseTFLService;
import ru.yandex.qatools.allure.annotations.Step;

import static com.frameworkium.integration.tfl.api.constant.Endpoint.JOURNEY_PLANNER;

public class ItineraryService extends BaseTFLService {

    @Step("Get itinerary from {0} to {1}")
    public Itinerary getItinerary(String from, String to) {
        return getRequestSpecification()
                .response().spec(getResponseSpecification())
                .get(JOURNEY_PLANNER.getUrl(from, to))
                .as(Itinerary.class);
    }

    @Step("Get national itinerary from {0} to {1}")
    public Itinerary getNationalItinerary(String from, String to) {
        return getRequestSpecification()
                .param("nationalSearch", "true")
                .response().spec(getResponseSpecification())
                .get(JOURNEY_PLANNER.getUrl(from, to))
                .as(Itinerary.class);
    }
}
