package tfl.services.journeyPlanner;

import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.core.api.services.ServiceFactory;
import com.jayway.restassured.specification.RequestSpecification;
import ru.yandex.qatools.allure.annotations.Step;
import tfl.entities.JourneyPlanner.DisambiguationResult;
import tfl.entities.JourneyPlanner.ItineraryResult;
import tfl.entities.JourneyPlanner.Journey;

import java.util.Arrays;

import static com.jayway.restassured.RestAssured.given;

public class JourneyPlannerService extends BaseService<JourneyPlannerService> {

    public static JourneyPlannerService newInstance(String from, String to) {
        return newInstance(from, to, new String[][]{});
    }

    public static JourneyPlannerService newInstance(String from, String to, String[][] params) {

        RequestSpecification reqSpec = given();
        for (String[] param : params) {
            reqSpec.param(param[0], param[1]);
        }
        return ServiceFactory.newInstance(
                JourneyPlannerService.class,
                "http://api.tfl.gov.uk/Journey/JourneyResults/" + from + "/to/" + to, reqSpec);
    }

    @Step
    public String getDisambiguatedFrom() {
        DisambiguationResult result = this.response.body().as(DisambiguationResult.class);
        if (result.fromLocationDisambiguation.matchStatus.equalsIgnoreCase("identified")) {
            return result.journeyVector.from;
        } else {
            return result.fromLocationDisambiguation.disambiguationOptions[0].parameterValue;
        }
    }

    @Step
    public String getDisambiguatedTo() {
        DisambiguationResult result = this.response.body().as(DisambiguationResult.class);
        if (result.toLocationDisambiguation.matchStatus.equalsIgnoreCase("identified")) {
            return result.journeyVector.to;
        } else {
            return result.toLocationDisambiguation.disambiguationOptions[0].parameterValue;
        }
    }

    @Step
    public Integer getShortestJourneyDuration() {
        ItineraryResult result = this.response.body().as(ItineraryResult.class);
        Journey[] journeys = result.journeys;
        Arrays.sort(journeys, (j1, j2) -> j1.duration.compareTo(j2.duration));
        return journeys[0].duration;
    }

}
