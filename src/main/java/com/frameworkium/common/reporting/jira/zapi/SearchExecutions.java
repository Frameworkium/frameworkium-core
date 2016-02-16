package com.frameworkium.common.reporting.jira.zapi;

import static com.frameworkium.config.SystemProperty.JIRA_URL;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.preemptive;

import java.util.ArrayList;
import java.util.List;

import com.frameworkium.config.SystemProperty;
import com.frameworkium.reporting.jira.Config;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.AuthenticationScheme;
import com.jayway.restassured.path.json.JsonPath;

public class SearchExecutions {

    private final static AuthenticationScheme auth = preemptive().basic(Config.jiraUsername, Config.jiraPassword);
    private final static String zapiURI = JIRA_URL.getValue() + Config.zapiRestURI;

    private final JsonPath jsonPath;

    public SearchExecutions(final String query) {
        RestAssured.baseURI = zapiURI;
        RestAssured.authentication = auth;

        jsonPath = get(String.format("zql/executeSearch?zqlQuery=%s", query)).andReturn().jsonPath();
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
