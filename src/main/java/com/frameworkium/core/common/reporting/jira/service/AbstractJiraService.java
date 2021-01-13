package com.frameworkium.core.common.reporting.jira.service;

import com.frameworkium.core.api.services.BaseService;
import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.common.reporting.jira.endpoint.JiraEndpoint;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;


public class AbstractJiraService extends BaseService {
    @Override
    protected RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .baseUri(JiraEndpoint.BASE_URI.getUrl())
                .relaxedHTTPSValidation()
                .auth().preemptive().basic(
                        Property.JIRA_USERNAME.getValue(),
                        Property.JIRA_PASSWORD.getValue());
    }

    @Override
    protected ResponseSpecification getResponseSpec() {
        throw new AssertionError("Unimplemented");
    }
}
