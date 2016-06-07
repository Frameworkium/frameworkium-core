package com.frameworkium.integration.tfl.api.test;

import com.frameworkium.core.api.tests.BaseTest;
import com.frameworkium.integration.tfl.api.dto.JourneyPlanner.DisambiguationResultDto;
import com.frameworkium.integration.tfl.api.dto.JourneyPlanner.ItineraryDto;
import com.frameworkium.integration.tfl.api.endpoint.journeyplanner.DisambiguationEndpoint;
import com.frameworkium.integration.tfl.api.endpoint.journeyplanner.ItineraryEndpoint;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;

public class JourneyPlannerTest extends BaseTest {

    @Test
    public void journey_planner_london_search_journey_duration() {
        DisambiguationResultDto disambiguationResult = new DisambiguationEndpoint().getDisambiguationResult(
                "Blue Fin Building, Southwark",
                "Waterloo Station, London"
        );

        String from = disambiguationResult.getJourneyVector().getFrom();
        String to = disambiguationResult.getJourneyVector().getTo();

        int shortestJourneyDuration = new ItineraryEndpoint().getItinerary(from, to).getShortestJourneyDuration();

        assertThat(shortestJourneyDuration).isLessThan(30);
    }

    @Test
    public void journey_planner_national_search_journey_duration() {
        ItineraryDto itineraryResult = new ItineraryEndpoint().getItinerary(
                "Blue Fin Building, Southwark",
                "Surrey Research Park, Guildford",
                true
        );

        int shortestJourneyDuration = itineraryResult.getShortestJourneyDuration();

        assertThat(shortestJourneyDuration).isGreaterThan(45);
    }

}
