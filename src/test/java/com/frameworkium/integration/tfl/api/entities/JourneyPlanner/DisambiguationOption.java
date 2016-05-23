package com.frameworkium.integration.tfl.api.entities.JourneyPlanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frameworkium.integration.tfl.api.entities.Place;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DisambiguationOption {
    public String parameterValue;
    public Place place;
    public Integer matchQuality;
}
