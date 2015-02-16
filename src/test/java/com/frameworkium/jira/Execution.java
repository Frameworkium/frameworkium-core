package com.frameworkium.jira;
import static com.frameworkium.config.SystemProperty.JIRA_RESULT_VERSION;
import static com.frameworkium.config.SystemProperty.JIRA_URL;
import static com.jayway.restassured.RestAssured.delete;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.preemptive;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.ITestResult;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.AuthenticationScheme;
import com.jayway.restassured.path.json.JsonPath;

public class Execution {

	private final static AuthenticationScheme auth = preemptive().basic(Config.jiraUsername,Config.jiraPassword);
	private final static String zapiURI = JIRA_URL.getValue() + Config.zapiRestURI;
	
	private final String version;
	private final String issue;
	private List<Integer> idList;
	private int status;
	
	
	public Execution(final String issue)
	{
		this.version = JIRA_RESULT_VERSION.getValue();
		this.issue = issue;
		this.idList = getExecutionIds();
	}
	
	public Execution(final String version, final String issue)
	{
		this.version = version;
		this.issue = issue;
		this.idList = getExecutionIds();
	}
	
	private List<Integer> getExecutionIds()
	{
		if (null == idList)
		{
			if(null != version && !version.isEmpty() && null != issue && !issue.isEmpty())
			{
				final String query = String.format("issue='%s' and fixVersion='%s'", issue, version);
				
				final SearchExecutions search = new SearchExecutions(query);
				idList=search.getExecutionIds();
				
				final List<Integer> statusList = search.getExecutionStatuses();
				if(statusList.size() > 0)
				{
					status = statusList.get(0);
				}
			}
		}
		return idList;
	}
	
	public int getExecutionStatus()
	{
		return status;
	}
	
	public void update(final int status, final String comment, final String attachment)
	{
		if (null != idList)
		{
			for (final Integer executionId : idList)
			{
				updateStatusAndComment(executionId, status, comment);
				
				if (null != attachment && !attachment.isEmpty())
				{
					deleteExistingAttachments(executionId);
					addAttachment(executionId, attachment);
				}
				
				System.out.println(String.format("JIRA Updater - Updated %s to status %s", issue, status));
				
			}
		}
	}
	
	
	private void updateStatusAndComment(final Integer executionId, final int status, final String comment)
	{
		final JSONObject obj = new JSONObject();
		try
		{
			obj.put("status", String.valueOf(status));
			obj.put("comment", comment);
		}
		catch (final JSONException e)
		{
			e.printStackTrace();
		}
		
		RestAssured.baseURI = zapiURI;
		RestAssured.authentication = auth;
		given().contentType("application/json").and().body(obj.toString()).then()
			.put("execution/" + executionId + "/execute");
	}
	
	private void deleteExistingAttachments(final Integer executionId)
	{
		final String url = String.format("attachment/attachmentsByEntity?entityId=%s&entityType=EXECUTION", executionId);
		
		RestAssured.baseURI = zapiURI;
		RestAssured.authentication = auth;
		
		final JsonPath jsonPath = get(url).andReturn().jsonPath();
		
		final List<String> fileIds = jsonPath.getList("data.fileId", String.class);
		
		//Iterate over attachments
		for (final String fileId : fileIds)
		{
			delete("attachment/" + fileId);
		}
	}
	
	
	private void addAttachment(final Integer executionId, final String attachment)
	{
		final String url = String.format("attachment?entityId=%s&entityType=EXECUTION", executionId);
		
		RestAssured.baseURI = zapiURI;
		RestAssured.authentication = auth;
		
		given().header("X-Atlassian-Token", "nocheck").and().multiPart(new File(attachment)).when().post(url);
	}
	
	//Converts ITestResult status to ZAPI execution status
	public static int getZAPIStatus(final int status)
	{
		switch (status)
		{
		case ITestResult.SUCCESS:
			return Config.ZAPI_STATUS.ZAPI_STATUS_PASS;
		case ITestResult.FAILURE:
			return Config.ZAPI_STATUS.ZAPI_STATUS_FAIL;
		case ITestResult.SKIP:
			return Config.ZAPI_STATUS.ZAPI_STATUS_BLOCKED;
		default:
			return Config.ZAPI_STATUS.ZAPI_STATUS_FAIL;
		}
	}
}
