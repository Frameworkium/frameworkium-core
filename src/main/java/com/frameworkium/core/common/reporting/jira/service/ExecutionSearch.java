package com.frameworkium.core.common.reporting.jira.service;

import com.frameworkium.core.common.reporting.jira.dto.executionsearch.ExecutionSearchDto;
import com.frameworkium.core.common.reporting.jira.endpoint.ZephyrEndpoint;
import io.restassured.specification.RequestSpecification;

public class ExecutionSearch extends AbstractJiraService {
    public ExecutionSearchDto search(String zqlQuery) {
        return search(zqlQuery, null, null, null, null);
    }

    public ExecutionSearchDto search(String zqlQuery, Integer filterId, Integer offset, Integer maxRecords, String expand) {
        RequestSpecification reqspec = getRequestSpec()
                .basePath(ZephyrEndpoint.EXECUTE_SEARCH.getUrl())
                .queryParam("zqlQuery", zqlQuery);

        if (filterId != null) reqspec.queryParam("filterId", filterId);
        if (offset != null) reqspec.queryParam("offset", offset);
        if (maxRecords != null) reqspec.queryParam("maxRecords", maxRecords);
        if (expand != null) reqspec.queryParam("expand", expand);

        return reqspec.when()
                .get().then().log().ifValidationFails()
                .extract()
                .as(ExecutionSearchDto.class);
    }
}
