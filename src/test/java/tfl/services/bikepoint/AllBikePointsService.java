package tfl.services.bikepoint;

import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.core.api.services.ServiceFactory;
import ru.yandex.qatools.allure.annotations.Step;
import tfl.entities.Place;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllBikePointsService extends BaseService<AllBikePointsService>{


    public static AllBikePointsService newInstance() {
        return ServiceFactory.newInstance(AllBikePointsService.class, "http://api.tfl.gov.uk/BikePoint");
    }

    /**
     * @return a list of common names
     */
    @Step
    public List<String> getAllNames() {
        List<String> names = new ArrayList<>();
        for (Place bikePoint : getAllBikePoints()){
            names.add(bikePoint.commonName);
        }
        return names;
    }

    /**
     * Create a list of all bikes points from the response.
     * @return a list of {@see BikePoints}
     */
    @Step
    public List<Place> getAllBikePoints() {
        Place[] bikePoints = this.response.body().as(Place[].class);
        return Arrays.asList(bikePoints);
    }

}
