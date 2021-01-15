package com.frameworkium.core.common.reporting.zephyr.service;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.common.reporting.zephyr.endpoint.ZephyrEndpoint;
import io.restassured.path.json.JsonPath;

import java.util.ArrayList;
import java.util.List;

public class SearchExecutions extends AbstractZephyrService {
    private final JsonPath jsonPath;

    /**
     * Constructor which executes the given query.
     */
    SearchExecutions(String query) {
        jsonPath = getRequestSpec().log().ifValidationFails()
                .basePath(ZephyrEndpoint.SEARCH.getUrl())
                .queryParam("zqlQuery", query)
                .when()
                .get().then().log().ifValidationFails()
                .extract().jsonPath();
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
