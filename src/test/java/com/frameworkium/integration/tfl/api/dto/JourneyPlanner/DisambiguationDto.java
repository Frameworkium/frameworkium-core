package com.frameworkium.integration.tfl.api.dto.JourneyPlanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DisambiguationDto {

    private List<DisambiguationOptionDto> disambiguationOptions;
    private String matchStatus;

    public List<DisambiguationOptionDto> getDisambiguationOptions() {
        return disambiguationOptions;
    }

    public void setDisambiguationOptions(List<DisambiguationOptionDto> disambiguationOptions) {
        this.disambiguationOptions = disambiguationOptions;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }
}
