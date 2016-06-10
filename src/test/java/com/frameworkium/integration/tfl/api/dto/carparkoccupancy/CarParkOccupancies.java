package com.frameworkium.integration.tfl.api.dto.carparkoccupancy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.Arrays;
import java.util.List;
import java.util.function.ToIntFunction;

import static java.util.stream.Collectors.toList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CarParkOccupancies {

    public List<CarParkOccupancy> carParkOccupancies;

    public CarParkOccupancies(CarParkOccupancy[] cpoArray) {
        this.carParkOccupancies = Arrays.asList(cpoArray);
    }

    @Step
    public Integer getTotalNumSpaces() {
        return getSumFromBays(bay -> bay.bayCount);
    }

    @Step
    public Integer getTotalNumFreeSpaces() {
        return getSumFromBays(bay -> bay.free);
    }

    @Step
    public Integer getTotalNumOccupiedSpaces() {
        return getSumFromBays(bay -> bay.occupied);
    }

    private int getSumFromBays(ToIntFunction<Bay> bayToIntFunction) {
        return carParkOccupancies.stream()         // create a Stream of the CPO array
                .flatMap(cpo -> cpo.bays.stream()) // flatMap (to a single Stream of Bays) each list of Bays in each CPO
                .mapToInt(bayToIntFunction)        // get the number of something from bays
                .sum();
    }

    @Step
    public List<String> getNames() {
        return carParkOccupancies.stream() // create a Stream of the CPO array
                .map(cpo -> cpo.name)      // get the name
                .collect(toList());        // collect the stream to a List
    }
}
