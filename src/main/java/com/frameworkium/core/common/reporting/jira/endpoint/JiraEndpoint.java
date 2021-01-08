package com.frameworkium.core.common.reporting.jira.endpoint;

import com.frameworkium.core.api.Endpoint;
import com.frameworkium.core.common.properties.Property;

public enum JiraEndpoint implements Endpoint {
    BASE_URI(Property.JIRA_URL.getValue() + "/rest/api/2"),
    SEARCH("/search"),
    ISSUE("/issue");

    JiraEndpoint(String url) {
        this.url = url;
    }

    private String url;

    @Override
    public String getUrl(final Object... params) {
        return url;
    }
}
