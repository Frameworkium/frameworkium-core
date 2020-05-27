package com.frameworkium.integration.capture.api.constant;

import com.frameworkium.lite.api.Endpoint;

/** The various Endpoints of Capture. */
public enum CaptureEndpoint implements Endpoint {

    BASE_URI("http://capture-6c06138r.cloudapp.net"),
    EXECUTIONS("/executions"),
    GET_EXECUTION("/executions/%s"),
    SUT_NAMES("/softwareUnderTest/names"),
    SUT_VERSIONS("/softwareUnderTest/versions/%s"),
    SCREENSHOT("/screenshot");

    private final String urlFormat;

    CaptureEndpoint(String urlFormat) {
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
