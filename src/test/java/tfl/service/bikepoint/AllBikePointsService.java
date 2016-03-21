package tfl.service.bikepoint;

import com.frameworkium.core.api.annotations.FindBy;
import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.core.api.services.ServiceFactory;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AllBikePointsService extends BaseService<AllBikePointsService> {

    @FindBy(jsonPath = "commonName")
    private List<String> allNames;

    public static AllBikePointsService newInstance() {
        Response r = RestAssured.get(BikePointResource.END_POINT);
        return ServiceFactory.newInstance(AllBikePointsService.class, r);
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
        List<String> lats = jsonPath.getList("lat", String.class);
        List<String> lons = jsonPath.getList("lon", String.class);

        return IntStream
                .range(0, allNames.size())
                .mapToObj(i -> new BikePoint(
                        allNames.get(i), lats.get(i), lons.get(i)))
                .collect(Collectors.toList());
    }

}
