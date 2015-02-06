package listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import annotations.Jira;
import org.apache.commons.lang3.ArrayUtils;
import org.testng.IAnnotationTransformer;
import org.testng.annotation.ITestAnnotation;

public class AnnotationListener implements IAnnotationTransformer{

  @SuppressWarnings("rawtypes")
  @Override
  public void transform(ItestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod)
  {
  	Jira jira = testMethod.getAnnotation(Jira.class);
  	if(null != jira)
  	{
  		String[] groups.ArrayUtils.addAll(new String[]{jira.value()},annotation.getGroups());
  		annotation.setGroups(groups);
  	}
  }
}
