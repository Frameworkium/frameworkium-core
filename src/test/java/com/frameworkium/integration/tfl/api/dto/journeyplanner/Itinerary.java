package com.frameworkium.integration.tfl.api.dto.journeyplanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Itinerary {

    public List<Journey> journeys;

    public int getShortestJourneyDuration() {
        return journeys.stream()
                .mapToInt(j -> j.duration)
                .min()
                .orElseThrow(IllegalStateException::new);
    }

}
