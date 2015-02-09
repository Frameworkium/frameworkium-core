package com.bootstrapium.annotations;

import java.lang.reflect.Method;

import org.testng.ITestNGMethod;

public class JiraUtils {
  public static Jira getJiraAnnotationOnTestMethod(ITestNGMethod method) {
    return getJiraAnnotationOnMethod(method.getConstructorOrMethod().getMethod());
  }
  
  public static Jira getJiraAnnotationOnMethod(Method method) {
    return method.getAnnotation(Jira.class);
  }
}
