package com.frameworkium.jira;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.ITestResult;


import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.AuthenticationScheme;
import com.jayway.restassured.path.json.JsonPath;

import static com.jayway.restassured.RestAssured.delete;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.preemptive;


public class Execution {

	public static class STATUS
	{
		public static final int ZAPI_STATUS_PASS = 1;
		public static final int ZAPI_STATUS_FAIL = 2;
		public static final int ZAPI_STATUS_WIP = 3;
		public static final int ZAPI_STATUS_BLOCKED = 4;
	}
	
	private final AuthenticationScheme auth = preemptive().basic("rgates","password1");
	private final String zapiURI = "http://localhost:8079/rest/zapi/latest";
	
	private final String version;
	private final String issue;
	private List<Integer> idList;
	private int status;
	
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
				
				System.out.println(String.format("Updated %s to status %s", issue, status));
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
			return STATUS.ZAPI_STATUS_PASS;
		case ITestResult.FAILURE:
			return STATUS.ZAPI_STATUS_FAIL;
		case ITestResult.SKIP:
			return STATUS.ZAPI_STATUS_BLOCKED;
		default:
			return STATUS.ZAPI_STATUS_FAIL;
		}
	}
}
