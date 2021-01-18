package com.frameworkium.core.common.reporting.zephyr.service;

import com.frameworkium.core.common.reporting.zephyr.endpoint.ZephyrEndpoint;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import static com.frameworkium.core.common.reporting.jira.JiraConfig.REST_ZAPI_PATH;
import static com.frameworkium.core.common.reporting.jira.JiraConfig.getJIRARequestSpec;

public class Execution extends AbstractZephyrService {
    public void updateExecution(String executionId) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("status", String.valueOf(status));
            int commentMaxLen = 750;
            obj.put("comment", StringUtils.abbreviate(comment, commentMaxLen));

            getJIRARequestSpec()
                    .contentType("application/json")
                    .body(obj.toString())
                    .when()
                    .put(REST_ZAPI_PATH + "execution/" + executionId + "/execute");

        } catch (JSONException e) {
            logger.error("Update status and comment failed", e);
        }

        getRequestSpec().log().ifValidationFails()
                .basePath(ZephyrEndpoint.EXECUTION.getUrl())
                .contentType(ContentType.JSON)
                .pathParam("executionId", executionId)
                .body(body)
                .when()
                .put("/{executionId}/execute")
                .then().log().ifValidationFails();


    }
}
