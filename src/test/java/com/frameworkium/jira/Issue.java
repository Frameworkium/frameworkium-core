package com.frameworkium.jira;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.AuthenticationScheme;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.preemptive;



public class Issue {

	protected final static AuthenticationScheme auth = preemptive().basic("rgates", "password1");
	protected final static String apiURI = "http://localhost:8079/rest/api/latest/";
	
	//Jira Key eg KT-123
	protected final String issue;
	
	public Issue(final String issue)
	{
		this.issue = issue;
	}
	
	public static void linkIssues(final String type, final String inwardIssue, final String outwardIssue)
	{
		final JSONObject obj = new JSONObject();
		final JSONObject typeObj = new JSONObject();
		final JSONObject inwardIssueObj = new JSONObject();
		final JSONObject outwardIssueObj = new JSONObject();
		
		try
		{
			obj.put("type", typeObj);
			typeObj.put("name", type);
			obj.put("inwardIssue", inwardIssueObj);
			inwardIssueObj.put("key", inwardIssue);
			obj.put("outwardIssue", outwardIssueObj);
			outwardIssueObj.put("key", outwardIssue);
		}
		catch (final JSONException e)
		{
			e.printStackTrace();
		}
		
		RestAssured.baseURI = apiURI;
		RestAssured.authentication = auth;
		
		given().contentType("application/json").and().body(obj.toString()).then().post("issueLink");
	}
	
	
	public List<String> getAttachmentIds()
	{
		RestAssured.baseURI = apiURI;
		RestAssured.authentication = auth;
		
		return get("issue/" + issue).andReturn().jsonPath().getList("fields.attachment.id");
	}
	
	public void addAttachment(final File attachment)
	{
		final String url = String.format("issue/%s/attachments", issue);
		
		RestAssured.baseURI = apiURI;
		RestAssured.authentication = auth;
		
		System.out.println(
				given().header("X-Atlassian-Token", "nocheck").and().multiPart(attachment)
					.and().log().all().when().post(url).andReturn().statusLine()
		);
	}
}
