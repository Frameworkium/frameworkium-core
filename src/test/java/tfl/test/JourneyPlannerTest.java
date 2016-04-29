package tfl.test;

import com.frameworkium.core.api.tests.BaseTest;
import org.testng.annotations.Test;
import tfl.services.journeyPlanner.JourneyPlannerService;

import static com.google.common.truth.Truth.assertThat;

public class JourneyPlannerTest extends BaseTest {

    @Test
    public void journey_planner_london_search_journey_duration() {

        JourneyPlannerService journeyPlannerService = JourneyPlannerService.newInstance("Blue Fin Building, Southwark", "Waterloo Station, London");

        String from = journeyPlannerService.getDisambiguatedFrom();
        String to = journeyPlannerService.getDisambiguatedTo();

        journeyPlannerService = journeyPlannerService.newInstance(from, to);

        assertThat(journeyPlannerService.getShortestJourneyDuration()).isLessThan(30);
    }

    @Test
    public void journey_planner_national_search_journey_duration() {

        JourneyPlannerService journeyPlannerService = JourneyPlannerService
                .newInstance("Blue Fin Building, Southwark", "Surrey Research Park, Guildford", new String[][]{{"nationalSearch","True"}});

        assertThat(journeyPlannerService.getShortestJourneyDuration()).isGreaterThan(45);

    }

}
