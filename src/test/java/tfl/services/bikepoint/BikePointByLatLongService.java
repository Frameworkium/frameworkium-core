package tfl.services.bikepoint;

import com.frameworkium.core.api.annotations.FindBy;
import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.core.api.services.ServiceFactory;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static tfl.services.bikepoint.BikePointResource.END_POINT;

public class BikePointByLatLongService extends BaseService<BikePointByLatLongService> {

    @FindBy(jsonPath = "places.commonName")
    private List<String> allNames;

    /**
     * Create a list of common names from the response.
     * @return a list of common names
     */
    public List<String> getAllNames() {
        return allNames;
    }

    public static BikePointByLatLongService newInstance(String lat, String lon, String radius) {
        RequestSpecification reqSpec = given()
                .param("lat", lat)
                .param("lon", lon)
                .param("radius", radius);
        return ServiceFactory.newInstance(BikePointByLatLongService.class, END_POINT, reqSpec);
    }
}
