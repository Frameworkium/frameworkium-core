package com.frameworkium.integration.restfulbooker.api.constant;

import com.frameworkium.lite.api.Endpoint;

/** The various Endpoints of Restful Booker. */
public enum BookerEndpoint implements Endpoint {

    BASE_URI("https://restful-booker.herokuapp.com"),
    PING("/ping"),
    BOOKING("/booking"),
    BOOKING_ID("/booking/%d"),
    AUTH("/auth");

    private final String urlFormat;

    BookerEndpoint(String urlFormat) {
        this.urlFormat = urlFormat;
    }

    /**
     * @param params Arguments referenced by the format specifiers in the url.
     * @return A formatted String representing the URL of the given constant.
     */
    @Override
    public String getUrl(Object... params) {
        return String.format(urlFormat, params);
    }

}
