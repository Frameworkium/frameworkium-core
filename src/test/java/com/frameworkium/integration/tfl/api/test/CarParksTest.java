package com.frameworkium.integration.tfl.api.test;

import com.frameworkium.core.api.tests.BaseTest;
import com.frameworkium.integration.tfl.api.dto.carparkoccupancy.CarParkOccupancies;
import com.frameworkium.integration.tfl.api.dto.carparkoccupancy.CarParkOccupancy;
import com.frameworkium.integration.tfl.api.service.carparks.CarParkOccupancyService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.google.common.truth.Truth.assertThat;

@Test
public class CarParksTest extends BaseTest {

    private CarParkOccupancies carParkOccupancies;

    @BeforeClass
    public void setUp() {
        carParkOccupancies = new CarParkOccupancyService().getCarParkOccupancies();
    }

    public void all_car_park_occupancies_more_than_10_spaces() {

        assertThat(carParkOccupancies.getTotalNumFreeSpaces())
                .isGreaterThan(10);
    }

    public void all_car_park_occupancies_contains_ruislip() {

        assertThat(carParkOccupancies.getNames())
                .contains("Ruislip Gardens Stn (LUL)");
    }

    public void single_car_park_request_information_the_same() {
        // N.B. this test might fail if the number of free/used bays changes
        // between the first and subsequent service call
        // this is due to using a live service and not controlling test data

        CarParkOccupancy randomCPO = getRandomCarParkOccupancy();

        // Get said CPO via ID
        CarParkOccupancy specificCPO =
                new CarParkOccupancyService().getCarParkOccupancy(randomCPO.id);

        // Make sure they are the same ignoring the details of the bays
        assertThat(specificCPO.equalsIgnoringBayDetails(randomCPO)).isTrue();
    }

    public void single_car_park_has_sane_number_of_free_spaces() {

        // Get said CP via ID
        int freeSpaces = new CarParkOccupancyService()
                .getCarParkOccupancy(getRandomCarParkOccupancy().id)
                .getNumFreeSpaces();

        // Make sure things are sane
        assertThat(freeSpaces).isAtLeast(1);
        assertThat(freeSpaces).isLessThan(10000);
    }

    @Test(enabled = false) // possible bug in the service
    public void free_and_occupied_equals_total() {

        int freeAndOccupiedCount =
                carParkOccupancies.getTotalNumFreeSpaces()
                        + carParkOccupancies.getTotalNumOccupiedSpaces();

        assertThat(carParkOccupancies.getTotalNumSpaces())
                .isEqualTo(freeAndOccupiedCount);
    }

    private CarParkOccupancy getRandomCarParkOccupancy() {

        List<CarParkOccupancy> cpos = carParkOccupancies.carParkOccupancies;
        // Pick a random CP from the list of all CPs
        return cpos.get(ThreadLocalRandom.current().nextInt(cpos.size()));
    }

}
