package com.frameworkium.integration.wiremock;

import com.frameworkium.core.api.tests.BaseAPITest;
import com.frameworkium.core.common.reporting.jira.endpoint.JiraEndpoint;
import com.frameworkium.core.common.reporting.jira.endpoint.ZephyrEndpoint;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.http.ContentType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Test
/* This is called in pre-integration-test phase to setup mocked jira responses for the integration-test phase
 * This mocked jira is specifically for tests that utilizes Frameworkium's Jira manipulation capabilities
 */
public class WireMockJiraSetup extends BaseAPITest {
    public void mockJiraSetup() {
        final WireMock wireMock = new WireMock();

        // zapi execution search
        // /rest/api/latest/executionSearch?zqlQuery=
        String searchExecutionUrlRegex = ZephyrEndpoint.EXECUTE_SEARCH.getUrl();
        final JSONObject obj = new JSONObject();
        final JSONArray executionsArray = new JSONArray();

        obj.put("executions", executionsArray);

        final JSONObject executionObj = new JSONObject();
        executionObj.put("id", 1);
        executionObj.put("cycleName", "myCycle");

        final JSONObject statusObj = new JSONObject();
        statusObj.put("id", 2);
        executionObj.put("status", statusObj);

        executionsArray.put(executionObj);

        wireMock.register(get(urlPathMatching(searchExecutionUrlRegex))
                .withQueryParam("zqlQuery", matching(".*"))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", ContentType.JSON.toString())
                        .withBody(obj.toString())
                )
        );

        // update zapi execution details
        // /rest/zapi/latest/execution/${id}/execute
        String updateExecutionDetailsUrlRegex = ZephyrEndpoint.EXECUTION.getUrl() + "/\\d+/execute";

        wireMock.register(put(urlPathMatching(updateExecutionDetailsUrlRegex))
                .withRequestBody(matchingJsonPath("$.status", matching(".*")))
                .withRequestBody(matchingJsonPath("$.comment", matching(".*")))
                .willReturn(aResponse().withStatus(200).withBody("{status:3}")
                        .withHeader("Content-Type", ContentType.JSON.toString())
                )
        );

        // get zapi attachment by entity
        // /rest/zapi/latest/attachment/attachmentsByEntity?entityId=&entityType=
        String getAttachmentByEntityUrlRegex = ZephyrEndpoint.ATTACHMENT_BY_ENTITY.getUrl();
        final JSONObject attachmentListBody = new JSONObject();
        final JSONArray attachmentArray = new JSONArray();
        final JSONObject attachmentObj1 = new JSONObject();
        final JSONObject attachmentObj2 = new JSONObject();
        attachmentListBody.put("data", attachmentArray);
        attachmentArray.put(attachmentObj1);
        attachmentArray.put(attachmentObj2);
        attachmentObj1.put("fileId", "943");
        attachmentObj2.put("fileId", "757");


        wireMock.register(get(urlPathMatching(getAttachmentByEntityUrlRegex))
                .withQueryParam("entityId", matching("\\d+"))
                .withQueryParam("entityType", equalTo("EXECUTION"))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", ContentType.JSON.toString())
                        .withBody(attachmentListBody.toString())
                )
        );

        // delete attachment
        // /rest/zapi/latest/attachment/${id}
        String deleteAttachmentUrlRegex = ZephyrEndpoint.ATTACHMENT.getUrl() + "/\\d+";
        wireMock.register(delete(urlPathMatching(deleteAttachmentUrlRegex))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", ContentType.JSON.toString())
                        .withBody("{\"success\": \"Attachment je-3.2.0.32002508-local.obr successfully deleted\"}")
                )
        );

        // add attachment
        // /rest/zapi/latest/attachment?entityId=&entityType=
        String addAttachmentUrlRegex = ZephyrEndpoint.ATTACHMENT.getUrl();
        wireMock.register(post(urlEqualTo(addAttachmentUrlRegex))
                .withQueryParam("entityId", matching("\\d+"))
                .withQueryParam("entityType", equalTo("EXECUTION"))
                .willReturn(aResponse().withStatus(200))
        );

        // get jira transition fields
        // GET /rest/api/2/issue/{issueIdOrKey}/transitions
        String getTransitionUrlRegex = JiraEndpoint.ISSUE.getUrl() + "/.*/transitions";
        final JSONObject transitionFieldBody = new JSONObject();
        final JSONArray transitionsArray = new JSONArray();
        final JSONObject transitionObj1 = new JSONObject();
        final JSONObject transitionObj2 = new JSONObject();
        final JSONObject transitionObj3 = new JSONObject();
        transitionFieldBody.put("transitions", transitionsArray);
        transitionsArray.put(transitionObj1);
        transitionsArray.put(transitionObj2);
        transitionsArray.put(transitionObj3);
        transitionObj1.put("id", "8309");
        transitionObj1.put("name", "Reopen");
        transitionObj2.put("id", "1346");
        transitionObj2.put("name", "Start Progress");
        transitionObj3.put("id", "9647");
        transitionObj3.put("name", "Done");
        // ["transitions": [["id": transitionId, "name": transitionName]]]

        wireMock.register(get(urlPathMatching(getTransitionUrlRegex))
                .withQueryParam("expand", equalTo("transitions.fields"))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", ContentType.JSON.toString())
                        .withBody(transitionFieldBody.toString())
                )
        );

        // Perform a transition on an issue
        // POST /rest/api/2/issue/{issueIdOrKey}/transitions
        String performTransitionUrlRegex = JiraEndpoint.ISSUE.getUrl() + "/.*/transitions";
        wireMock.register(post(urlPathMatching(performTransitionUrlRegex))
                //    ["transition": [ "id": "5" ]]
                .withRequestBody(matchingJsonPath("$.transition.id"))
                .willReturn(aResponse().withStatus(204))
        );

        // Add comment to Jira issue
        // POST /rest/api/2/issue/{issueIdOrKey}/comment
        String addCommentUrlRegex = JiraEndpoint.ISSUE.getUrl() + "/.*/comment";
        wireMock.register(post(urlPathMatching(addCommentUrlRegex))
                // ["body": "lorem ipsum"]
                .withRequestBody(matchingJsonPath("$.body"))
                .willReturn(aResponse().withStatus(201)
                        .withHeader("Content-Type", ContentType.JSON.toString())
                )
        );

        // get jira field
        // GET /rest/api/2/field
        String getFieldUrlRegex = JiraEndpoint.FIELD.getUrl();
        final JSONArray fieldArray = new JSONArray();
        final JSONObject fieldObj = new JSONObject();
        fieldArray.put(fieldObj);
        fieldObj.put("id", "jiraFieldId");
        fieldObj.put("name", "jiraFieldName");
        wireMock.register(get(urlPathMatching(getFieldUrlRegex))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", ContentType.JSON.toString())
                        .withBody(fieldArray.toString())
                )
        );

        // edit issue
        // PUT /rest/api/2/issue/{issueIdOrKey}
        String editIssueUrlRegex = JiraEndpoint.ISSUE.getUrl() + "/.*";
        // '{"update":{"133":[{"set":{"value":"Fail"}}]}}'
        wireMock.register(put(urlPathMatching(editIssueUrlRegex))
                .withRequestBody(matchingJsonPath("$.update.*.[*].set"))
                .willReturn(aResponse().withStatus(204)
                )
        );
    }
}
