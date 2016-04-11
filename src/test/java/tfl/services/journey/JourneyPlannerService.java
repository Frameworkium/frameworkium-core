package tfl.services.journey;

import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.core.api.services.ServiceFactory;
import com.jayway.restassured.specification.RequestSpecification;

import static com.jayway.restassured.RestAssured.given;

public class JourneyPlannerService extends BaseService<JourneyPlannerService> {

//    @FindBy(jsonPath = "commonName")
//    private List<String> allNames;

    public static JourneyPlannerService newInstance(String from, String to) {
        return newInstance(from, to, new String[][]{});
    }
    public static JourneyPlannerService newInstance(String from, String to, String[][] params) {

        RequestSpecification reqSpec = given();
        for(String[] param : params){
            reqSpec.param(param[0], param[1]);
        }
        return ServiceFactory.newInstance(JourneyPlannerService.class, "http://api.tfl.gov.uk/Journey/JourneyResults/" + from + "/to/" + to, reqSpec);
    }


}
