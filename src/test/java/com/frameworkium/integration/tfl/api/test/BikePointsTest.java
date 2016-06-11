package com.frameworkium.integration.tfl.api.test;

import com.frameworkium.core.api.tests.BaseTest;
import com.frameworkium.integration.tfl.api.dto.bikepoints.BikePoints;
import com.frameworkium.integration.tfl.api.dto.common.Place;
import com.frameworkium.integration.tfl.api.service.bikepoints.BikePointService;
import com.frameworkium.integration.tfl.api.service.bikepoints.BikePointsParamsBuilder;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.TestCaseId;

import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

@Test
public class BikePointsTest extends BaseTest {

    private BikePoints bikePoints;

    @BeforeClass
    public void setUp() {
        bikePoints = new BikePointService().getBikePoints();
    }

    @TestCaseId("BP-1")
    public void all_bikes_contains_something_and_there_are_a_lot_of_them() {

        List<String> allNames = bikePoints.getAllNames();

        assertThat(allNames).contains("Evesham Street, Avondale");
        assertThat(allNames.size()).isAtLeast(700);
    }

    @TestCaseId("BP-2")
    public void given_lat_long_of_point_point_appears_in_lat_long_search() {

        // Get random bike point
        Place randomBP = bikePoints.getRandomBikePoint();

        // Search for lat long of said bike point with 200m radius
        Map<String, String> params =
                new BikePointsParamsBuilder()
                        .latitude(randomBP.lat)
                        .longditude(randomBP.lon)
                        .radiusInMeters(200)
                        .build();

        BikePoints searchResults = new BikePointService().searchBikePoints(params);

        // Then said bike point is part of result set
        assertThat(searchResults.getAllNames())
                .contains(randomBP.commonName);
    }
}
