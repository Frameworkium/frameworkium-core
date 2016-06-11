package com.frameworkium.integration.tfl.api.service.carparks;

import com.frameworkium.integration.tfl.api.dto.carparkoccupancy.CarParkOccupancies;
import com.frameworkium.integration.tfl.api.dto.carparkoccupancy.CarParkOccupancy;
import com.frameworkium.integration.tfl.api.service.BaseTFLService;
import ru.yandex.qatools.allure.annotations.Step;

import static com.frameworkium.integration.tfl.api.constant.Endpoint.CAR_PARK_OCCUPANCY;
import static com.frameworkium.integration.tfl.api.constant.Endpoint.CAR_PARK_OCCUPANCY_BY_ID;

public class CarParkOccupancyService extends BaseTFLService {

    @Step("Get Car Park Occupancies")
    public CarParkOccupancies getCarParkOccupancies() {
        CarParkOccupancy[] carParkOccupancies =
                performRequest(CAR_PARK_OCCUPANCY.getUrl())
                        .as(CarParkOccupancy[].class);
        return new CarParkOccupancies(carParkOccupancies);
    }

    @Step("Get Car Park Occupancy by ID {0}")
    public CarParkOccupancy getCarParkOccupancyByID(String id) {
        return performRequest(CAR_PARK_OCCUPANCY_BY_ID.getUrl(id))
                .as(CarParkOccupancy.class);
    }

}
