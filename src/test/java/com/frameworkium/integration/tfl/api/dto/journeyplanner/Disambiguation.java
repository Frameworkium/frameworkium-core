package com.frameworkium.integration.tfl.api.dto.journeyplanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Disambiguation {

    public List<DisambiguationOption> disambiguationOptions;
    public String matchStatus;
}
