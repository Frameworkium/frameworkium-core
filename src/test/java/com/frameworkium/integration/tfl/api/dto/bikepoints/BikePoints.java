package com.frameworkium.integration.tfl.api.dto.bikepoints;

import com.frameworkium.integration.tfl.api.dto.common.Place;
import com.frameworkium.integration.tfl.api.dto.common.PlacesResponse;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class BikePoints {

    private List<Place> bikePoints;

    public BikePoints(Place[] places) {
        this.bikePoints = Arrays.asList(places);
    }

    public BikePoints(PlacesResponse placesResponse) {
        this.bikePoints = placesResponse.places;
    }

    /** @return a list of common names */
    @Step
    public List<String> getAllNames() {
        return bikePoints.stream()
                .map(bp -> bp.commonName)
                .collect(Collectors.toList());
    }

    @Step
    public Place getRandomBikePoint() {
        return bikePoints.get(
                ThreadLocalRandom.current().nextInt(bikePoints.size()));
    }

}
