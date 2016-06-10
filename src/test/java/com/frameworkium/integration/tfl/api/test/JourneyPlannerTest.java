package com.frameworkium.integration.tfl.api.test;

import com.frameworkium.core.api.tests.BaseTest;
import com.frameworkium.integration.tfl.api.dto.journeyplanner.DisambiguationResult;
import com.frameworkium.integration.tfl.api.dto.journeyplanner.Itinerary;
import com.frameworkium.integration.tfl.api.service.journeyplanner.DisambiguationService;
import com.frameworkium.integration.tfl.api.service.journeyplanner.ItineraryService;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;

public class JourneyPlannerTest extends BaseTest {

    @Test
    public void journey_planner_london_search_journey_duration() {
        DisambiguationResult disambiguationResult = new DisambiguationService()
                .getDisambiguationResult(
                        "Blue Fin Building, Southwark",
                        "Waterloo Station, London"
                );

        String from = disambiguationResult.journeyVector.from;
        String to = disambiguationResult.getFirstDisambiguatedTo();

        int shortestJourneyDuration = new ItineraryService()
                .getItinerary(from, to)
                .getShortestJourneyDuration();

        assertThat(shortestJourneyDuration).isLessThan(30);
    }

    @Test
    public void journey_planner_national_search_journey_duration() {
        Itinerary itineraryResult = new ItineraryService()
                .getNationalItinerary(
                        "Blue Fin Building, Southwark",
                        "Surrey Research Park, Guildford"
                );

        int shortestJourneyDuration = itineraryResult.getShortestJourneyDuration();

        assertThat(shortestJourneyDuration).isGreaterThan(45);
    }

}
