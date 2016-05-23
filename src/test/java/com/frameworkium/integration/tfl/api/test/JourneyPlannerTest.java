package com.frameworkium.integration.tfl.api.test;

import com.frameworkium.core.api.tests.BaseTest;
import com.frameworkium.integration.tfl.api.services.journeyPlanner.JourneyPlannerDisambiguationResponse;
import com.frameworkium.integration.tfl.api.services.journeyPlanner.JourneyPlannerItineraryResponse;
import com.frameworkium.integration.tfl.api.services.journeyPlanner.JourneyPlannerService;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class JourneyPlannerTest extends BaseTest {

    @Test
    public void journey_planner_london_search_journey_duration() {

        JourneyPlannerDisambiguationResponse disambiguationResponse =
                JourneyPlannerService.newInstance(
                        JourneyPlannerDisambiguationResponse.class,
                        "Blue Fin Building, Southwark",
                        "Waterloo Station, London"
                );

        String from = disambiguationResponse.getFrom();
        String to = disambiguationResponse.getFirstDisambiguatedTo();

        int shortestJourneyDuration = JourneyPlannerService
                .newInstance(JourneyPlannerItineraryResponse.class, from, to)
                .getShortestJourneyDuration();

        assertThat(shortestJourneyDuration).isLessThan(30);
    }

    @Test
    public void journey_planner_national_search_journey_duration() {

        Map<String, String> params = ImmutableMap.of("nationalSearch", "True");

        JourneyPlannerItineraryResponse response =
                JourneyPlannerService.newInstance(
                        JourneyPlannerItineraryResponse.class,
                        "Blue Fin Building, Southwark",
                        "Surrey Research Park, Guildford",
                        params
                );

        int shortestJourneyDuration = response.getShortestJourneyDuration();

        assertThat(shortestJourneyDuration).isGreaterThan(45);
    }

}
