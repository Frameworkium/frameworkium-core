package com.frameworkium.integration.tfl.api.dto.journeyplanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Journey {

    public Integer duration;
    public List<Leg> legs;
}
