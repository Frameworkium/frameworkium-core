package com.bootstrapium.listeners;

import java.util.ArrayList;
import java.util.List;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import com.bootstrapium.config.DriverType;

public class MethodInterceptor implements IMethodInterceptor {

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods,
            ITestContext context) {
        List<IMethodInstance> methodsToRun = new ArrayList<IMethodInstance>();
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
