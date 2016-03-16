package com.frameworkium.core.common.reporting.spira;

import com.frameworkium.core.common.properties.CommonProperty;
import com.frameworkium.core.ui.properties.UIProperty;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.ITestResult;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static com.jayway.restassured.RestAssured.given;

public class SpiraExecution {

    private final static Logger logger = LogManager.getLogger(SpiraExecution.class);

    private final static String spiraURI = CommonProperty.SPIRA_URL.getValue() + Config.spiraRestURI;
    private final String username = Config.spiraUsername;
    private final String apiKey = Config.spiraApiKey;

    public void recordTestResult(final String issue, final int resultId, final String comment,final ITestResult result) {

        JSONObject obj = new JSONObject();
        try {
            obj.put("ExecutionStatusId",0);
            obj.put("StartDate","\\/Date(" + result.getStartMillis() + ")\\/");
            obj.put("TestCaseId",issue.replaceAll("[^0-9]",""));
            obj.put("TestRunTypeId",0);
            obj.put("TestRunFormatId",0);
            obj.put("ExecutionStatusId",resultId);

            obj.put("RunnerName","Frameworkium");
            obj.put("RunnerTestName",result.getMethod().getMethodName());
            obj.put("RunnerStackTrace",comment);

            if(CommonProperty.RESULT_VERSION.isSpecified()){
                obj.put("ReleaseId", getReleaseId(CommonProperty.RESULT_VERSION.getValue()));
            }
            //"Name":null,
            //"BuildId":null,
            //"RunnerMessage":null,
        } catch (JSONException e) {
            logger.error("Update status and comment failed", e);
        }

        //JSONObject auto-escapes the backslash, so we need to unescape it!
        String json = obj.toString().replace("\\\\", "\\");

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(spiraURI + "/test-runs/record?" +
                "username=" + username + "&" +
                "api-key=" + apiKey.replace("{", "%7B").replace("}", "%7D"));
        StringEntity jsonBody;
        try {
            jsonBody = new StringEntity(json);
            request.addHeader("content-type", "application/json");
            request.setEntity(jsonBody);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            httpClient.execute(request);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public String getReleaseId(String releaseName) {
        RestAssured.baseURI = spiraURI;
        
        JsonPath jsonPath = given().contentType("application/json")
                .with().parameter("username", username)
                .and().parameter("api-key", apiKey)
                .get("/releases").andReturn().jsonPath();

        return jsonPath.getString(String.format("find {it.FullName == '%s'}.ReleaseId", releaseName));
    }

}


