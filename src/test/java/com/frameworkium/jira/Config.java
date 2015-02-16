package com.frameworkium.jira;


public class Config {

	public static final boolean FailTestOnlyIfAssertionError = true;
	
	public static final String jiraUsername = "rgates";
	public static final String jiraPassword = "password1";
		
	public static final String jiraRestURI = "/rest/api/latest";
	public static final String zapiRestURI = "/rest/zapi/latest";
	
	public static class ZAPI_STATUS
	{
		public static final int ZAPI_STATUS_PASS = 1;
		public static final int ZAPI_STATUS_FAIL = 2;
		public static final int ZAPI_STATUS_WIP = 3;
		public static final int ZAPI_STATUS_BLOCKED = 4;
	}

}
