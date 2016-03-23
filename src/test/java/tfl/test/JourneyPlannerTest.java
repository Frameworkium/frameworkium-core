package tfl.test;

import com.frameworkium.core.api.tests.BaseTest;
import org.testng.annotations.Test;
import tfl.services.bikepoint.AllBikePointsService;
import tfl.services.bikepoint.BikePointByLatLongService;
import tfl.services.journey.JourneyPlannerService;

import java.util.List;
import java.util.Random;

import static com.google.common.truth.Truth.assertThat;

public class JourneyPlannerTest extends BaseTest {

    @Test
    public void journey_planner_test_no_delays_on_journey() {

        JourneyPlannerService journeyPlannerService = JourneyPlannerService.newInstance("-0.2180,51.5114","-0.1388,51.5416");

        String b =
                journeyPlannerService.prettyPrintResponse();

        journeyPlannerService.resendLastRequest();

       // assertThat(journeyPlannerService.getAllNames()).contains("Evesham Street, Avondale");

//        assertThat(journeyPlannerService.getAllNames().size()).isAtLeast(700);
    }

}
