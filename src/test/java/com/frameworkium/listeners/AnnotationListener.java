package com.frameworkium.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.apache.commons.lang3.ArrayUtils;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import com.frameworkium.annotations.Jira;

public class AnnotationListener implements IAnnotationTransformer{

  @SuppressWarnings("rawtypes")
  @Override
  public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod)
  {
  	Jira jira = testMethod.getAnnotation(Jira.class);
  	if(null != jira)
  	{
  		String[] groups = ArrayUtils.addAll(new String[]{jira.value()},annotation.getGroups());
  		annotation.setGroups(groups);
  	}
  }
}
