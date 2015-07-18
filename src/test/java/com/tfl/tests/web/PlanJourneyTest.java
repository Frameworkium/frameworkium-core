package com.tfl.tests.web;

import static com.google.common.truth.Truth.assertThat;

import org.testng.annotations.Test;

import ru.yandex.qatools.allure.annotations.Issue;

import com.frameworkium.tests.internal.BaseTest;
import com.tfl.pages.web.HomePage;
import com.tfl.pages.web.JourneyPlannerResultsPage;
import com.tfl.pages.web.PlanJourneyPage;

public class PlanJourneyTest extends BaseTest {

    @Issue("TFL-1")
    @Test(description = "Plan a journey test")
    public final void planJourneyTest() {

        // Navigate to homepage then click the the plan journey link
        PlanJourneyPage planJourneyPage = HomePage.open().then().clickPlanJourneyLink();

        // Plan a journey between two locations
        JourneyPlannerResultsPage resultsPage = planJourneyPage.planJourney("Clapham Junction", "Oxford Circus");
        
        // Check that the title displayed on the page is "JOURNEY RESULTS"
        assertThat(resultsPage.getTitleText()).isEqualTo("JOURNEY RESULTS");
        
    }
}
