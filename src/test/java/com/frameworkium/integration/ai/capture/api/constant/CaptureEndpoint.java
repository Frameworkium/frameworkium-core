package com.frameworkium.integration.ai.capture.api.constant;

import com.frameworkium.core.api.Endpoint;

/** The various Endpoints of Capture. */
public enum CaptureEndpoint implements Endpoint {

    BASE_URI("http://ec2-35-176-47-3.eu-west-2.compute.amazonaws.com"),
    EXECUTIONS("/executions"),
    GET_EXECUTION("/executions/%s"),
    SUT_NAMES("/softwareUnderTest/names"),
    SUT_VERSIONS("/softwareUnderTest/versions/%s"),
    SCREENSHOT("/screenshot");

    private String url;

    CaptureEndpoint(String url) {
        this.url = url;
    }

    /**
     * @param params Arguments referenced by the format specifiers in the url.
     * @return A formatted String representing the URL of the given constant.
     */
    @Override
    public String getUrl(Object... params) {
        return String.format(url, params);
    }

}
