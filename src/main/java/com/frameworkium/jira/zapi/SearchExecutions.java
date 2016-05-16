package com.frameworkium.jira.zapi;

import static com.frameworkium.config.SystemProperty.JIRA_URL;
import static com.jayway.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.List;

import com.frameworkium.config.SystemProperty;
import com.frameworkium.jira.Config;
import com.jayway.restassured.path.json.JsonPath;

public class SearchExecutions {

    private final static String zapiURI = JIRA_URL.getValue() + Config.zapiRestURI;

    private final JsonPath jsonPath;

    public SearchExecutions(final String query) {

        jsonPath = given()
                .auth(). preemptive().basic(Config.jiraUsername, Config.jiraPassword)
                .baseUri(zapiURI)
                .get(String.format("zql/executeSearch?zqlQuery=%s", query))
                .andReturn().jsonPath();
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

    public List<String> getIssueIds(){
        return getIDs("executions.issueId");
    }

    private <T> List<T> getIDs(String path) {

        List<T> list = new ArrayList<>();
        List<T> tempList = jsonPath.getList(path);

        if (SystemProperty.ZAPI_CYCLE_REGEX.isSpecified()) {
            String jiraCycleRegEx = SystemProperty.ZAPI_CYCLE_REGEX.getValue();
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
