package com.frameworkium.listeners;

import java.util.ArrayList;
import java.util.List;

import static com.frameworkium.config.SystemProperty.JQL_QUERY;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import com.frameworkium.config.DriverType;

import com.frameworkium.jira.SearchExecutions;

public class MethodInterceptor implements IMethodInterceptor {

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods,
            ITestContext context) {
        List<IMethodInstance> methodsToRun = new ArrayList<IMethodInstance>();
        
        
        if(JQL_QUERY.isSpecified())
        {
        		
 
        //	List<String> jiraIssues = SearchIssues(System.getProperty("JQL_QUERY").toString()).getKeys();
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
 
if (!context.getAttribute("Issue").toString().isEmpty())
{
	
}
        
        
        
        
        
        
        
        return methodsToRun;
    }

}
