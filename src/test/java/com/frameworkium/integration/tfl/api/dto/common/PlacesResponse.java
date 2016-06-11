package com.frameworkium.integration.tfl.api.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlacesResponse {
    public double[] centrePoint;
    public List<Place> places;
}
