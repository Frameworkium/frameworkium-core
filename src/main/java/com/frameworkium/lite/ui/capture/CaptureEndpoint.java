package com.frameworkium.lite.ui.capture;

import com.frameworkium.lite.api.Endpoint;
import com.frameworkium.lite.common.properties.Property;

/** The various Endpoints of Capture. */
enum CaptureEndpoint implements Endpoint {

    BASE_URI(Property.CAPTURE_URL.getValue()),
    EXECUTIONS(BASE_URI.urlFormat + "/executions"),
    SCREENSHOT(BASE_URI.urlFormat + "/screenshot");

    private final String urlFormat;

    CaptureEndpoint(String urlFormat) {
        this.urlFormat = urlFormat;
    }

    @Override
    public String getUrl(Object... params) {
        return String.format(urlFormat, params);
    }

}
