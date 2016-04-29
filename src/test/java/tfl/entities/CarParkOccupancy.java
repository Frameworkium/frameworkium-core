package tfl.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by robertgates55 on 25/04/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CarParkOccupancy {
    public String id;
    public Bay[] bays;
    public String name;

}
