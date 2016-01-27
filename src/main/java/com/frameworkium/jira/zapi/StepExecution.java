package com.frameworkium.jira.zapi;

import com.frameworkium.jira.Config;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.jayway.restassured.path.json.JsonPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.frameworkium.config.SystemProperty.JIRA_URL;
import static com.jayway.restassured.RestAssured.given;

/**
 * Created by civie21 on 08/12/2015.
 */
public class StepExecution {


    final static Logger logger = LogManager.getLogger(TestExecution.class);
    final static String zapiURI = JIRA_URL.getValue() + Config.zapiRestURI;

    private final String version;
    private final String issue;
    private String issueId;
    private final int step;
    private List<Integer> stepIds;


    public StepExecution(final String version, final String issue, final String step) {
        this.version = version;
        this.issue = issue;
        this.step = Integer.valueOf(step);
        this.issueId = getIssueId();
        this.stepIds = getStepIds();
    }

    private String getIssueId() {

        if (null == issueId) {
            List<String> issueIdList;
            if (null != version && !version.isEmpty() && null != issue && !issue.isEmpty()) {
                String query = String.format("issue='%s' and fixVersion='%s'", issue, version);

                SearchExecutions search = new SearchExecutions(query);
                issueIdList = search.getIssueIds();
                issueId = issueIdList.get(0);

            }
        }
        return issueId;
    }

    private List<Integer> getStepIds() {
        if (null == stepIds) {
            if (0 != step && null != issue && !issue.isEmpty()) {
                String testStepsURI = zapiURI + "/teststep/"+issueId;

                JsonPath steps = given()
                        .contentType("application/json")
                        .auth().preemptive().basic(Config.jiraUsername, Config.jiraPassword)
                        .get(testStepsURI).andReturn().jsonPath();

                stepIds  = steps.getList("id");
            }
        }
        return stepIds;
    }

    public void updateStepStatusAndComment(final Integer stepdId, final int status, String comment) {

        JSONObject obj = new JSONObject();
        try {
            obj.put("status", String.valueOf(status));

            //Limit on Zephyr's comment field capacity
            if (comment.length() > 750) {
                comment = comment.substring(0, 747) + "...";
            }
            obj.put("comment", comment);

        } catch (JSONException e) {
            logger.error("Update step status and comment failed", e);
        }


        given().contentType("application/json")
                .auth().preemptive().basic(Config.jiraUsername, Config.jiraPassword)
                .baseUri(zapiURI)
                .body(obj.toString())
                .put("stepResult/" + stepdId);

    }

    public void update(final int status, final String comment) {
        if (null != issueId) {
            updateStepStatusAndComment(stepIds.get(step-1), status, comment);
            logger.debug("ZAPI Updater - Updated step %s in test %s to status %s", step, issue, status);
            checkAndExecuteTest();
        }
    }

    public void checkAndExecuteTest() {

        ArrayList<String> statusList = new ArrayList<>();
        for(Integer step:stepIds){
            statusList.add(given().contentType("application/json")
                            .auth().preemptive().basic(Config.jiraUsername, Config.jiraPassword)
                            .get(zapiURI+"/stepResult/" + step)
                            .as(JsonObject.class,ObjectMapperType.GSON)
                            .get("status").getAsString());
        }
        TestExecution executionBasedOnStepResults = new TestExecution(version, issue);
        if (statusList.contains("-1")) {
            executionBasedOnStepResults.update(Config.ZAPI_STATUS.ZAPI_STATUS_UNEXECUTED,
                    "One or more steps are unexecuted, therefore this test has been marked as unexecuted", null);
        } else if (statusList.contains("2")) {
            executionBasedOnStepResults.update(Config.ZAPI_STATUS.ZAPI_STATUS_FAIL,
                    "One or more steps have failed, therefore this test has been marked as failed", null);
        } else if (statusList.contains("4")) {
            executionBasedOnStepResults.update(Config.ZAPI_STATUS.ZAPI_STATUS_BLOCKED,
                    "One or more steps have been blocked, therefore this test has been marked as blocked", null);
        } else {
            executionBasedOnStepResults.update(Config.ZAPI_STATUS.ZAPI_STATUS_PASS,
                    "All steps have passed!", null);
        }
    }
}
