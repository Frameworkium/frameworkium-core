package tfl.entities.JourneyPlanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Disambiguation {
    public DisambiguationOption[] disambiguationOptions;
    public String matchStatus;
}
