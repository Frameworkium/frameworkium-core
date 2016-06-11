package com.frameworkium.integration.tfl.api.service.bikepoints;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

/**
 * A builder object for parameters to the BikePointsService.
 * This makes test maintainable easier if parameter identifiers change but not
 * their logical meaning.
 */
public class BikePointsParamsBuilder {

    private Map<String, String> bikePointParams = new HashMap<>();

    public BikePointsParamsBuilder latitude(String lat) {
        bikePointParams.put("lat", lat);
        return this;
    }

    public BikePointsParamsBuilder longditude(String lon) {
        bikePointParams.put("lon", lon);
        return this;
    }

    public BikePointsParamsBuilder radiusInMeters(long radius) {
        bikePointParams.put("radius", String.valueOf(radius));
        return this;
    }

    public Map<String, String> build() {
        return ImmutableMap.copyOf(bikePointParams);
    }
}
