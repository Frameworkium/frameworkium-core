package com.frameworkium.integration.tfl.api.dto.journeyplanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frameworkium.integration.tfl.api.dto.common.Place;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DisambiguationOption {

    public String parameterValue;
    public Place place;
    public Integer matchQuality;

}
