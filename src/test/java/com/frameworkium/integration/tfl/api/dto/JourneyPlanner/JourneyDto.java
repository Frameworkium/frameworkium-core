package com.frameworkium.integration.tfl.api.dto.JourneyPlanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JourneyDto {

    private Integer duration;
    private List<LegDto> legs;

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public List<LegDto> getLegs() {
        return legs;
    }

    public void setLegs(List<LegDto> legs) {
        this.legs = legs;
    }
}
