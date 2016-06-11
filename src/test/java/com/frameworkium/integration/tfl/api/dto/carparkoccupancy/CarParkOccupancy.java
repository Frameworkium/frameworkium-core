package com.frameworkium.integration.tfl.api.dto.carparkoccupancy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CarParkOccupancy {

    public String id;
    public List<Bay> bays;
    public String name;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    public boolean equalsIgnoringBays(Object obj) {
        CarParkOccupancy other = (CarParkOccupancy) obj;
        return EqualsBuilder.reflectionEquals(this, other, "bays")
                && bays.size() == other.bays.size();
    }

    @Step
    public int getNumFreeSpaces() {
        return bays.stream()
                .mapToInt(bay -> bay.free)
                .sum();
    }
}
