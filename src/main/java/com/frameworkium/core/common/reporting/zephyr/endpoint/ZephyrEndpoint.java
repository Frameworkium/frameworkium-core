package com.frameworkium.core.common.reporting.zephyr.endpoint;

import com.frameworkium.core.api.Endpoint;
import com.frameworkium.core.common.properties.Property;

public enum ZephyrEndpoint implements Endpoint {
    BASE_URI(Property.JIRA_URL.getValue()),
    REST_API_PATH("/rest/zapi/latest"),
    EXECUTION("/execution"),
    ATTACHMENT("/attachment"),
    ATTACHMENT_BY_ENTITY("/attachment/attachmentsByEntity"),
    SEARCH("/zql/executeSearch");

    ZephyrEndpoint(String url) {
        this.url = url;
    }

    private final String url;

    /**
     * @param params Arguments referenced by the format specifiers in the url.
     * @return A formatted String representing the URL of the given constant.
     */
    @Override
    public String getUrl(Object... params) {
        if (url.equals(REST_API_PATH.url) || url.equals(BASE_URI.url)) {
            return String.format(url, params);
        }
        // returns with the rest API path e.g. /rest/zapi/2/issue
        String urlWithRestAPIPath = REST_API_PATH.url + url;
        return String.format(urlWithRestAPIPath, params);
    }
}