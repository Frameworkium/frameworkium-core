package com.frameworkium.core.common.listeners;

import com.frameworkium.core.common.reporting.jira.api.SearchIssues;
import com.frameworkium.core.ui.driver.DriverType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.TestCaseId;

import java.lang.reflect.Method;
import java.util.*;

import static com.frameworkium.core.common.properties.Property.JIRA_URL;
import static com.frameworkium.core.common.properties.Property.JQL_QUERY;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class MethodInterceptor implements IMethodInterceptor {

    private static final Logger logger = LogManager.getLogger(MethodInterceptor.class);
    private static boolean interceptMethodsBasedOnName = false;

    @Override
    public List<IMethodInstance> intercept(
            List<IMethodInstance> methods, ITestContext context) {

        List<IMethodInstance> methodsToRun = filterTestsToRunByJQL(methods);
        if (interceptMethodsBasedOnName) {
            logger.info("Filtering tests based on their name.");
            return filterTestsToRunByDriverAndTestClassName(methodsToRun);
        } else {
            return methodsToRun;
        }
    }

    private List<IMethodInstance> filterTestsToRunByJQL(
            List<IMethodInstance> methodsToBeFiltered) {

        if (JQL_QUERY.isSpecified() && JIRA_URL.isSpecified()) {
            // gets list of tests to run from JIRA based on the JQL
            logger.info("Overriding specified tests to run with JQL query results");

            Map<String, IMethodInstance> testMethods = new HashMap<>();
            for (IMethodInstance iMethod : methodsToBeFiltered) {
                Method method = iMethod.getMethod().getConstructorOrMethod().getMethod();
                Issue issueAnnotation = method.getAnnotation(Issue.class);
                if (null != issueAnnotation) {
                    testMethods.put(issueAnnotation.value(), iMethod);
                } else {
                    TestCaseId testCaseIdAnnotation = method.getAnnotation(TestCaseId.class);
                    if (null != testCaseIdAnnotation) {
                        testMethods.put(testCaseIdAnnotation.value(), iMethod);
                    }
                }
            }

            List<IMethodInstance> methodsToRun = new ArrayList<>();
            List<String> issueKeysBasedOnJQL = new SearchIssues(JQL_QUERY.getValue()).getKeys();
            for (String issueKey : issueKeysBasedOnJQL) {
                IMethodInstance method = testMethods.get(issueKey);
                if (null != method) {
                    methodsToRun.add(method);
                    logger.trace("Adding " + issueKey);
                } else {
                    logger.error("Key cannot find test for issueKey " + issueKey);
                }
            }

            logger.debug("Running the following test methods:\n{}", () ->
                    methodsToRun.stream()
                            .map(m -> m.getMethod().getMethodName())
                            .collect(joining(", ")));
            logger.info("Running {} tests specified by JQL query...", methodsToRun.size());

            return methodsToRun;
        } else {
            // Can't run the JQL without both JIRA_URL and JQL_QUERY
            return methodsToBeFiltered;
        }
    }

    //TODO - make this non-UI specific!
    private List<IMethodInstance> filterTestsToRunByDriverAndTestClassName(
            List<IMethodInstance> methods) {

        return methods.stream()
                .filter(instance -> {
                    String clazz = instance.getMethod().getRealClass().getName();

                    boolean appTest = clazz.endsWith("AppTest");
                    boolean mobiTest = clazz.endsWith("MobiTest");
                    boolean nonMobileNotAppMobiTest =
                            !DriverType.isMobile() && !appTest && !mobiTest;
                    boolean nativeAppTest =
                            DriverType.isNative() && appTest;
                    boolean nonNativeMobiTest =
                            !DriverType.isNative() && mobiTest;

                    return nonMobileNotAppMobiTest || nativeAppTest || nonNativeMobiTest;
                })
                .collect(toList());
    }

}
