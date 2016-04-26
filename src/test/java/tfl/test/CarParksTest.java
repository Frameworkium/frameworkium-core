package tfl.test;

import com.frameworkium.core.api.tests.BaseTest;
import org.testng.annotations.Test;
import tfl.entities.CarParkOccupancy;
import tfl.services.carparkoccupancy.CarParkOccupancyService;

import java.util.Random;

import static com.google.common.truth.Truth.assertThat;

public class CarParksTest extends BaseTest {



    @Test
    public void all_car_park_occupancies_more_than_10_spaces() {

        CarParkOccupancyService carParkOccupancyService = CarParkOccupancyService.newInstance();
        assertThat(carParkOccupancyService.sumFreeSpaces(carParkOccupancyService.getCarParkOccupancies())).isGreaterThan(10);

    }

    @Test
    public void all_car_park_occupancies_contains_ruislip() {

        CarParkOccupancyService carParkOccupancyService = CarParkOccupancyService.newInstance();
        assertThat(carParkOccupancyService.getCarParkNames()).contains("Ruislip Gardens Stn (LUL)");

    }

    @Test
    public void single_car_park_request_information_the_same() {

        CarParkOccupancyService carParkOccupancyService = CarParkOccupancyService.newInstance();
        CarParkOccupancy randomCPO = carParkOccupancyService.getCarParkOccupancies().get(new Random().nextInt(carParkOccupancyService.getCarParkOccupancies().size()));
        CarParkOccupancyService specificCarParkQuery = CarParkOccupancyService.newInstance(randomCPO.id);
        CarParkOccupancy specificCPO = specificCarParkQuery.getCarParkOccupancies().get(0);
        assertThat(specificCPO).isEqualTo(randomCPO);

    }

}
