package com.frameworkium.core.common.reporting.jira.endpoint;

import com.frameworkium.core.api.Endpoint;
import com.frameworkium.core.common.properties.Property;

public enum JiraEndpoint implements Endpoint {
    BASE_URI(Property.JIRA_URL.getValue()),
    REST_API_PATH("/rest/api/2"),
    PROJECT("/project"),
    SEARCH("/search"),
    ISSUE("/issue"),
    ISSUELINK("/issueLink"),
    FIELD("/field"),
    VERSION("/version");

    JiraEndpoint(String url) {
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
            return url;
        }
        // returns with the rest API path e.g. /rest/api/2/issue
        String urlWithRestAPIPath = REST_API_PATH.url + url;
        return String.format(urlWithRestAPIPath, params);
    }
}
