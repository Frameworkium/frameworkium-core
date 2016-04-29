package tfl.services.carparkoccupancy;

import com.frameworkium.core.api.annotations.DeserialiseAs;
import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.core.api.services.ServiceFactory;
import ru.yandex.qatools.allure.annotations.Step;
import tfl.entities.Bay;
import tfl.entities.CarParkOccupancy;

public class CarParkOccupancySingleService extends BaseService<CarParkOccupancySingleService> {

    @DeserialiseAs
    private CarParkOccupancy carParkOccupancy;


    public static CarParkOccupancySingleService newInstance(String id) {
        return ServiceFactory.newInstance(
                CarParkOccupancySingleService.class,
                String.format("https://api.tfl.gov.uk/Occupancy/CarPark/%s", id));
    }

    @Step
    public CarParkOccupancy getCPO() {
        return carParkOccupancy;
    }

    @Step
    public String getCarParkName() {
        return carParkOccupancy.name;
    }

    @Step
    public int getNumFreeSpaces() {
        int totalFree = 0;
        for (Bay bay : carParkOccupancy.bays) {
            totalFree += bay.free;
        }
        return totalFree;
    }
}
