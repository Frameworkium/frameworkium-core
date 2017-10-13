package com.frameworkium.core.common.reporting.jira.zapi;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.common.reporting.jira.JiraConfig;
import io.restassured.path.json.JsonPath;

import java.util.ArrayList;
import java.util.List;

public class SearchExecutions {

    private final JsonPath jsonPath;

    /**
     * Constructor with the execution query.
     * @param query
     */
    public SearchExecutions(final String query) {

        jsonPath = JiraConfig.getJIRARequestSpec()
                .when()
                .get(JiraConfig.REST_ZAPI_PATH + "zql/executeSearch?zqlQuery=" + query)
                .thenReturn().jsonPath();
    }

    /**
     * Get execution IDs.
     *
     * @return the execution Ids returned by the search.
     */
    public List<Integer> getExecutionIds() {

        return getIDs("executions.id");
    }

    /**
     * Get status IDs.
     *
     * @return the execution status Ids returned by the search.
     */
    public List<Integer> getExecutionStatuses() {

        return getIDs("executions.status.id");
    }

    private List<Integer> getIDs(String path) {

        List<Integer> list = new ArrayList<>();
        List<Integer> tempList = jsonPath.getList(path);

        if (Property.ZAPI_CYCLE_REGEX.isSpecified()) {
            String jiraCycleRegEx = Property.ZAPI_CYCLE_REGEX.getValue();
            List<String> cycleNames = jsonPath.getList("executions.cycleName");
            for (int i = 0; i < cycleNames.size(); i++) {
                if (cycleNames.get(i).contains(jiraCycleRegEx)) {
                    list.add(tempList.get(i));
                }
            }
        } else {
            list = tempList;
        }
        return list;
    }
}
