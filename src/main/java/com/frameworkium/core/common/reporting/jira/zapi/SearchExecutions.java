package com.frameworkium.core.common.reporting.jira.zapi;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.common.reporting.jira.Config;
import io.restassured.path.json.JsonPath;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;


public class SearchExecutions {

    private final static String zapiURI = Property.JIRA_URL.getValue() + Config.zapiRestURI;
    private final JsonPath jsonPath;

    public SearchExecutions(final String query) {

        jsonPath = given().auth().preemptive().basic(Config.jiraUsername, Config.jiraPassword)
                .when()
                .get(zapiURI + String.format("zql/executeSearch?zqlQuery=%s", query))
                .thenReturn().jsonPath();
    }

    /**
     * @return the execution Ids returned by the search.
     */
    public List<Integer> getExecutionIds() {

        return getIDs("executions.id");
    }

    /**
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
