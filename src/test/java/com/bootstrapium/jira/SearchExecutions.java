package com.bootstrapium.jira;

import static com.jayway.restassured.RestAssured.preemptive;

import java.util.ArrayList;
import java.util.List;

import static com.jayway.restassured.RestAssured.get;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.AuthenticationScheme;
import com.jayway.restassured.path.json.JsonPath;

public class SearchExecutions {

	private final AuthenticationScheme auth = preemptive().basic("rgates","password1");
	private final String zapiURI = "http://localhost:8079/rest/zapi/latest";
	
	private final JsonPath jsonPath;
	
	public SearchExecutions(final String query)
	{
		RestAssured.baseURI = zapiURI;
		RestAssured.authentication = auth;
		
		jsonPath = get(String.format("zql/executeSearch?zqlQuery=%s", query)).andReturn().jsonPath();
	}
	
	//Returns the execution Ids returned by the search
	public List<Integer> getExecutionIds()
	{
//TODO		final String browser = getBrowserNameAndVersion();
		final String browser = "FF10";
		
		final List<Integer> tempExecutionIds = jsonPath.getList("executions.id");
		final List<Integer> executionIds = new ArrayList<Integer>();
		final List<String> cycleNames = jsonPath.getList("executions.cycleName");
		for (int idx = 0; idx < cycleNames.size(); idx++)
		{
			if (cycleNames.get(idx).contains(browser))
			{
				executionIds.add(tempExecutionIds.get(idx));
			}
		}
		return executionIds;
	}
	
	//Returns the execution statuses returned by the search
	public List<Integer> getExecutionStatuses()
	{
//TODO		final String browser = getBrowserNameAndVersion();
		final String browser = "FF10";
		
		final List<Integer> tempExecutionStatuses = jsonPath.getList("executions.status.id");
		final List<Integer> executionStatuses = new ArrayList<Integer>();
		final List<String> cycleNames = jsonPath.getList("executions.cycleName");
		for (int idx = 0; idx < cycleNames.size(); idx++)
		{
			if (cycleNames.get(idx).contains(browser))
			{
				executionStatuses.add(tempExecutionStatuses.get(idx));
			}
		}
		return executionStatuses;
	}	
}
