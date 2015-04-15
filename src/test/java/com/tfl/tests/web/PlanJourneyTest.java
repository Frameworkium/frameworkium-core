package com.tfl.tests.web;

import org.testng.annotations.Test;

import ru.yandex.qatools.allure.annotations.Issue;

import com.frameworkium.tests.internal.BaseTest;
import com.tfl.pages.web.HomePage;
import com.tfl.pages.web.JourneyPlannerResultsPage;
import com.tfl.pages.web.PlanJourneyPage;

public class PlanJourneyTest extends BaseTest {

    @Issue("TFL-1")
    @Test(description = "Plan a journey test")
    public final void componentExampleTest() {

        // Navigate to homepage then use the nav bar to go to the plan journey page
        PlanJourneyPage planJourneyPage = HomePage.open().then().with().theNavBar().clickPlanJourneyLink();

        // Plan a journey between two locations
        JourneyPlannerResultsPage resultsPage = planJourneyPage.planJourney("Clapham Junction", "Oxford Circus");
        
        //Disambiguate results
        resultsPage.disambiguateFromAndTo();
        
        System.out.println(resultsPage.getShortestJourneyTimeInMinutes());

    }
}
