package com.frameworkium.integration.tfl.api.service.carparks;

import com.frameworkium.integration.tfl.api.dto.carparkoccupancy.CarParkOccupancies;
import com.frameworkium.integration.tfl.api.dto.carparkoccupancy.CarParkOccupancy;
import com.frameworkium.integration.tfl.api.service.BaseTFLService;
import ru.yandex.qatools.allure.annotations.Step;

import static com.frameworkium.integration.tfl.api.constant.Endpoint.CAR_PARK_OCCUPANCY;

public class CarParkOccupancyService extends BaseTFLService {

    @Step("Get Car Park Occupancies")
    public CarParkOccupancies getCarParkOccupancies() {
        return new CarParkOccupancies(
                getResponseSpecification()
                        .get(CAR_PARK_OCCUPANCY.getUrl())
                        .as(CarParkOccupancy[].class));
    }
}
