package tfl.services.bikepoint;

import com.frameworkium.core.api.annotations.DeserialiseAs;
import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.core.api.services.ServiceFactory;
import ru.yandex.qatools.allure.annotations.Step;
import tfl.entities.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class BikePointsService extends BaseService<BikePointsService>{

    @DeserialiseAs
    private Place[] bikePoints;

    public static BikePointsService newInstance() {
        return ServiceFactory.newInstance(BikePointsService.class, "http://api.tfl.gov.uk/BikePoint");
    }

    /**
     * @return a list of common names
     */
    @Step
    public List<String> getAllNames() {

        List<String> names = new ArrayList<>();
        for (Place bikePoint : bikePoints){
            names.add(bikePoint.commonName);
        }
        return names;
    }

    @Step
    public Place getRandomBikePoint(){
        return bikePoints[new Random().nextInt(bikePoints.length)];
    }


}
