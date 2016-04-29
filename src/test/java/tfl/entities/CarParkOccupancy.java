package tfl.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CarParkOccupancy {
    public String id;
    public Bay[] bays;
    public String name;
}
