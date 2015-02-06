package jira;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.preemptive;

import java.util.List;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.AuthenticationScheme;
import com.jayway.restassured.path.json.JsonPath;

public class SearchIssues {

	private final AuthenticationScheme auth = preemptive().basic("rgates","password1");
	private final String zapiURI = "http://localhost:8079/rest/zapi/latest";
	
	private final JsonPath jsonPath;
	
	public SearchIssues(final String query)
	{
		RestAssured.baseURI = zapiURI;
		RestAssured.authentication = auth;
		
		jsonPath = get(String.format("search?jql=%s&maxResults=1000", query)).andReturn().jsonPath();
	}
	
	public List<String> getKeys()
	{
		return jsonPath.getList("issues.key");
	}
	
	
	public List<String> getSummaries()
	{
		return jsonPath.getList("issues.fields.summary");
	}
	
	
	public String getKEyForSummary(final String summary)
	{
		return jsonPath.getString(String.format("issues.find {it.fields.summary == '%s'}.key", summary));
	}
}
