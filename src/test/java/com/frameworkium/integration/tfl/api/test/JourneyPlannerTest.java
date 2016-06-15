package com.frameworkium.integration.tfl.api.test;

import com.frameworkium.core.api.tests.BaseTest;
import com.frameworkium.integration.tfl.api.dto.journeyplanner.DisambiguationResult;
import com.frameworkium.integration.tfl.api.service.journeyplanner.DisambiguationService;
import com.frameworkium.integration.tfl.api.service.journeyplanner.ItineraryService;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.TestCaseId;

import static com.google.common.truth.Truth.assertThat;

public class JourneyPlannerTest extends BaseTest {

    @Test
    @TestCaseId("JP-API-1")
    public void journey_planner_london_search_journey_duration() {
        DisambiguationResult disambiguationResult = new DisambiguationService()
                .getDisambiguationResult(
                        "Blue Fin Building, Southwark",
                        "Waterloo Station, London"
                );

        __stepStart("arbitrary allure step");
        String from = disambiguationResult.journeyVector.from;
        String to = disambiguationResult.getFirstDisambiguatedTo();
        __stepFinish();

        int shortestJourneyDuration = new ItineraryService()
                .getNationalItinerary(from, to)
                .getShortestJourneyDuration();

        assertThat(shortestJourneyDuration).isLessThan(30);
    }

}
