package com.frameworkium.integration.tfl.api.entities.JourneyPlanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Journey {
    public Integer duration;
    public Leg[] legs;
}
