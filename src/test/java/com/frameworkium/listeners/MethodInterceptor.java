package com.frameworkium.listeners;

import static com.frameworkium.config.SystemProperty.JIRA_URL;
import static com.frameworkium.config.SystemProperty.JQL_QUERY;

import java.util.ArrayList;
import java.util.List;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import ru.yandex.qatools.allure.annotations.Issue;

import com.frameworkium.config.DriverType;
import com.frameworkium.jira.SearchIssues;

public class MethodInterceptor implements IMethodInterceptor {

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods,
            ITestContext context) {
  
    	
    	List<IMethodInstance> methodsToRun = new ArrayList<IMethodInstance>();
        
		if(JQL_QUERY.isSpecified() && JIRA_URL.isSpecified())
		{
			System.out.println("Overriding specified tests to run with JQL query results");
			methodsToRun = new ArrayList<IMethodInstance>();
			List<String> groups = null;
			
			//Search for tests
			groups = new SearchIssues(JQL_QUERY.getValue()).getKeys();
			
			for (String issueKey : groups)
			{	
			
				for (IMethodInstance instance : methods) {
		
					if(instance.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Issue.class) != null)
					{
						String issueId = instance.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Issue.class).value();
					
						if(issueId.equals(issueKey))
						{
							System.out.println(String.format("Adding %s",issueKey));
							methodsToRun.add(instance);
						}
					}
				}

			}
			
			System.out.println(String.format("Running %s tests specified by query...", methodsToRun.size()));
			
			methods = methodsToRun;
		} 
        
        for (IMethodInstance instance : methods) {
        	
            String clazz = instance.getMethod().getRealClass().getName();
            if (!DriverType.isMobile()) {
            	if(clazz.endsWith("WebTest")) {
            		methodsToRun.add(instance);
            	}
            } else {
            	if(DriverType.isNative() && clazz.endsWith("AppTest")) {
            		methodsToRun.add(instance);
            	}
            	else if(!DriverType.isNative() && clazz.endsWith("MobiTest")){
            		methodsToRun.add(instance);
            	}
            }   
            
            
        }
  
        return methodsToRun;
    }

}
