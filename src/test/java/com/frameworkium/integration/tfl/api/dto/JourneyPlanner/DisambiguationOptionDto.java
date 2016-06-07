package com.frameworkium.integration.tfl.api.dto.JourneyPlanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frameworkium.integration.tfl.api.dto.PlaceDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DisambiguationOptionDto {

    private String parameterValue;
    private PlaceDto place;
    private Integer matchQuality;

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    public PlaceDto getPlace() {
        return place;
    }

    public void setPlace(PlaceDto place) {
        this.place = place;
    }

    public Integer getMatchQuality() {
        return matchQuality;
    }

    public void setMatchQuality(Integer matchQuality) {
        this.matchQuality = matchQuality;
    }

}
