package com.frameworkium.core.common.reporting.jira.zapi;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.common.reporting.jira.JiraConfig;
import io.restassured.path.json.JsonPath;

import java.util.ArrayList;
import java.util.List;

public class SearchExecutions {

    private final JsonPath jsonPath;

    /**
     * Constructor which executes the given query.
     */
    SearchExecutions(String query) {
        jsonPath = JiraConfig.getJIRARequestSpec()
                .when()
                .get(JiraConfig.REST_ZAPI_PATH + "zql/executeSearch?zqlQuery=" + query)
                .thenReturn().jsonPath();
    }

    public List<Integer> getExecutionIds() {
        return getIDs("executions.id");
    }

    public List<Integer> getExecutionStatuses() {
        return getIDs("executions.status.id");
    }

    private List<Integer> getIDs(String path) {
        List<Integer> ids = jsonPath.getList(path);

        if (Property.ZAPI_CYCLE_REGEX.isSpecified()) {
            List<Integer> filteredIds = new ArrayList<>();
            String jiraCycleRegEx = Property.ZAPI_CYCLE_REGEX.getValue();
            List<String> cycleNames = jsonPath.getList("executions.cycleName");
            for (int i = 0; i < cycleNames.size() && i < ids.size(); i++) {
                if (cycleNames.get(i).contains(jiraCycleRegEx)) {
                    filteredIds.add(ids.get(i));
                }
            }
            return filteredIds;
        } else {
            return ids;
        }
    }
}
