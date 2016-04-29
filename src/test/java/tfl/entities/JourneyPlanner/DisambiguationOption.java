package tfl.entities.JourneyPlanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tfl.entities.Place;

/**
 * Created by robertgates55 on 26/04/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DisambiguationOption {
    public String parameterValue;
    public Place place;
    public Integer matchQuality;
}
