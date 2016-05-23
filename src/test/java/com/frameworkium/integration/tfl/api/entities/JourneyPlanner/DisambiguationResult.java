package com.frameworkium.integration.tfl.api.entities.JourneyPlanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DisambiguationResult {
    public Disambiguation toLocationDisambiguation;
    public Disambiguation fromLocationDisambiguation;
    public JourneyVector journeyVector;
}
