package com.frameworkium.integration.tfl.api.constant;

public enum Endpoint {

    JOURNEY_PLANNER("https://api.tfl.gov.uk/journey/JourneyResults/%s/to/%s");

    private String url;

    Endpoint(String url) {
        this.url = url;
    }

    public String getUrl(String[] param) {
        return String.format(url, param);
    }

}
