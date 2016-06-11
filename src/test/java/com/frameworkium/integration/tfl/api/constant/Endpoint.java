package com.frameworkium.integration.tfl.api.constant;

public enum Endpoint {

    BASE_URI("https://api.tfl.gov.uk"),
    JOURNEY_PLANNER("/journey/JourneyResults/%s/to/%s"),
    CAR_PARK_OCCUPANCY("/Occupancy/CarPark"),
    CAR_PARK_OCCUPANCY_BY_ID("/Occupancy/CarPark/%s"),
    BIKE_POINT("/BikePoint");

    private String url;

    Endpoint(String url) {
        this.url = url;
    }

    /**
     * Calls {@link String#format(String, Object...)} on the url.
     *
     * @param params Arguments referenced by the format specifiers in the url.
     * @return A formatted URL String
     */
    public String getUrl(Object... params) {
        return String.format(url, params);
    }

}
