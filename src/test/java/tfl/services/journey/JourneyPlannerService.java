package tfl.services.journey;

import com.frameworkium.core.api.annotations.FindBy;
import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.core.api.services.ServiceFactory;
import com.gargoylesoftware.htmlunit.javascript.host.fetch.Request;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ResponseBody;
import com.jayway.restassured.response.ValidatableResponseOptions;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.requestSpecification;

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
        return ServiceFactory.newInstance(JourneyPlannerService.class, JourneyResource.END_POINT+"/"+from+"/to/"+to, reqSpec);
    }

    /**
     * @return a list of common names
//     */
//    public List<String> getAllNames() {
//
//        given().get("wer").then().statusCode(200);
//        //return allNames;
//    }


}
