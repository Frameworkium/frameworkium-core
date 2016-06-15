package com.frameworkium.integration.tfl.api.constant;

import com.frameworkium.core.api.Endpoint;

public enum TFLEndpoint implements Endpoint {

    BASE_URI("https://api.tfl.gov.uk"),
    JOURNEY_PLANNER("/journey/JourneyResults/%s/to/%s");

    private String url;

    TFLEndpoint(String url) {
        this.url = url;
    }

    @Override
    public String getUrl(Object... params) {
        return String.format(url, params);
    }

}
