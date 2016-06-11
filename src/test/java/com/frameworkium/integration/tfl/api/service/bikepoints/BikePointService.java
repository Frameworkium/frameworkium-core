package com.frameworkium.integration.tfl.api.service.bikepoints;

import com.frameworkium.integration.tfl.api.dto.bikepoints.BikePoints;
import com.frameworkium.integration.tfl.api.dto.common.Place;
import com.frameworkium.integration.tfl.api.dto.common.PlacesResponse;
import com.frameworkium.integration.tfl.api.service.BaseTFLService;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.Map;

import static com.frameworkium.integration.tfl.api.constant.Endpoint.BIKE_POINT;

public class BikePointService extends BaseTFLService {

    @Step("Get Bike Points")
    public BikePoints getBikePoints() {
        return new BikePoints(
                getResponseSpecification()
                        .get(BIKE_POINT.getUrl())
                        .as(Place[].class));
    }

    @Step("Search Bike Points")
    public BikePoints searchBikePoints(Map<String, String> params) {
        return new BikePoints(
                getResponseSpecification(params)
                        .get(BIKE_POINT.getUrl())
                        .as(PlacesResponse.class));
    }
}
