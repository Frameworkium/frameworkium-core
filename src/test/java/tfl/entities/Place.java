package tfl.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by robertgates55 on 26/04/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Place {
    public String commonName;
    public String lat;
    public String lon;
}
