package tfl.test;

import com.frameworkium.core.api.tests.BaseTest;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.TestCaseId;
import tfl.entities.Place;
import tfl.services.bikepoint.AllBikePointsService;
import tfl.services.bikepoint.BikePointByLatLongService;

import java.util.List;
import java.util.Random;

import static com.google.common.truth.Truth.assertThat;

public class BikePointsTest extends BaseTest {


    @Test
    public void all_bikes_contains_something_and_there_are_a_lot_of_them2() {

        AllBikePointsService bps = AllBikePointsService.newInstance();

        assertThat(bps.getAllNames()).contains("Evesham Street, Avondale");
        assertThat(bps.getAllNames().size()).isAtLeast(700);
    }


    @Test
    public void all_bikes_contains_something_and_there_are_a_lot_of_them() {

        AllBikePointsService allBikePoints = AllBikePointsService.newInstance();

        assertThat(allBikePoints.getAllNames()).contains("Evesham Street, Avondale");
        assertThat(allBikePoints.getAllNames().size()).isAtLeast(700);
    }

    @Test
    @TestCaseId("TEST-1")
    public void given_lat_long_of_point_point_appears_in_lat_long_search() {

        // Get random bike point
        AllBikePointsService allBikePoints = AllBikePointsService.newInstance();
        List<Place> bikePoints = allBikePoints.getAllBikePoints();

        Place randomBP = bikePoints.get(new Random().nextInt(bikePoints.size()));

        // Search for lat long of said bike point with 200m radius
        BikePointByLatLongService latLongBikePoints =
                BikePointByLatLongService.newInstance(randomBP.lat, randomBP.lon, "200");

        // Then said bike point is part of result set
        assertThat(latLongBikePoints.getAllNames()).contains(randomBP.commonName);
    }
}