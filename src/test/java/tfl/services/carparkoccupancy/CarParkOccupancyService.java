package tfl.services.carparkoccupancy;

import com.frameworkium.core.api.annotations.DeserialiseAs;
import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.core.api.services.ServiceFactory;
import ru.yandex.qatools.allure.annotations.Step;
import tfl.entities.Bay;
import tfl.entities.CarParkOccupancy;

import java.util.ArrayList;
import java.util.List;

public class CarParkOccupancyService extends BaseService<CarParkOccupancyService> {

    @DeserialiseAs
    private CarParkOccupancy[] carParkOccupancies;

    public static CarParkOccupancyService newInstance() {
        return ServiceFactory.newInstance(
                CarParkOccupancyService.class, "https://api.tfl.gov.uk/Occupancy/CarPark");
    }

    @Step
    public CarParkOccupancy[] getAllCPOs() {
        return carParkOccupancies;
    }

    @Step
    public List<String> getCarParkNames() {
        List<String> carParkNames = new ArrayList<>();
        for (CarParkOccupancy cpo : carParkOccupancies) {
            carParkNames.add(cpo.name);
        }
        return carParkNames;
    }

    @Step
    public int getNumFreeSpaces() {
        int totalFree = 0;

        for (CarParkOccupancy cpo : carParkOccupancies) {
            for (Bay bay : cpo.bays) {
                totalFree += bay.free;
            }
        }
        return totalFree;
    }
}
