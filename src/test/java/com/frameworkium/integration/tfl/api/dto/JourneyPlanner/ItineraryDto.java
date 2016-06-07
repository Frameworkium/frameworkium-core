package com.frameworkium.integration.tfl.api.dto.JourneyPlanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ItineraryDto {

    private List<JourneyDto> journeys;

    public List<JourneyDto> getJourneys() {
        return journeys;
    }

    public void setJourneys(List<JourneyDto> journeys) {
        this.journeys = journeys;
    }

    public int getShortestJourneyDuration() {
        assert getJourneys().size() > 0;
        return getJourneys().stream()
                .mapToInt(JourneyDto::getDuration)
                .min()
                .orElseThrow(IllegalStateException::new);
    }

}
