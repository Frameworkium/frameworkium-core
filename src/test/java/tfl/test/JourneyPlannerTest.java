package tfl.test;

import com.frameworkium.core.api.tests.BaseTest;
import org.testng.annotations.Test;
import tfl.services.journey.JourneyPlannerService;

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
