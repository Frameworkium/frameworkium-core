package tfl.services.bikepoint;

import com.frameworkium.core.api.annotations.FindBy;
import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.core.api.services.ServiceFactory;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jayway.restassured.RestAssured.given;

public class AllBikePointsService extends BaseService<AllBikePointsService> {

    @FindBy(jsonPath = "commonName")
    private List<String> allNames;

    @FindBy(jsonPath = "lat")
    private List<String> allLats;

    @FindBy(jsonPath = "lon")
    private List<String> allLongs;

    public static AllBikePointsService newInstance() {
        return ServiceFactory.newInstance(AllBikePointsService.class, BikePointResource.END_POINT);
    }

    /**
     * Class describing a bike point
     */
    public class BikePoint {
        public String name;
        public String lat;
        public String lon;

        public BikePoint(String name, String lat, String lon) {
            this.name = name;
            this.lat = lat;
            this.lon = lon;
        }
    }

    /**
     * @return a list of common names
     */
    public List<String> getAllNames() {
        return allNames;
    }

    /**
     * Create a list of all bikes points from the response.
     * @return a list of {@see BikePoints}
     */
    public List<BikePoint> getAllBikePoints() {

        return IntStream
                .range(0, allNames.size())
                .mapToObj(i -> new BikePoint(
                        allNames.get(i), allLats.get(i), allLongs.get(i)))
                .collect(Collectors.toList());
    }

}
