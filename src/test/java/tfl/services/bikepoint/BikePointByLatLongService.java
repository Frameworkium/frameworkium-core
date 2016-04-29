package tfl.services.bikepoint;

import com.frameworkium.core.api.annotations.FindBy;
import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.core.api.services.ServiceFactory;
import com.jayway.restassured.specification.RequestSpecification;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.List;

import static com.jayway.restassured.RestAssured.given;

public class BikePointByLatLongService extends BaseService<BikePointByLatLongService> {

    @FindBy(jsonPath = "places.commonName")
    private List<String> allNames;


    public static BikePointByLatLongService newInstance(String lat, String lon, String radius) {
        RequestSpecification reqSpec = given()
                .param("lat", lat)
                .param("lon", lon)
                .param("radius", radius);
        return ServiceFactory.newInstance(
                BikePointByLatLongService.class, "http://api.tfl.gov.uk/BikePoint", reqSpec);
    }

    /**
     * Create a list of common names from the response.
     * @return a list of common names
     */
    @Step
    public List<String> getAllNames() {
        return allNames;
    }
}
