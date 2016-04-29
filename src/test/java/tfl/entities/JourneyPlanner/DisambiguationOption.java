package tfl.entities.JourneyPlanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tfl.entities.Place;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DisambiguationOption {
    public String parameterValue;
    public Place place;
    public Integer matchQuality;
}
