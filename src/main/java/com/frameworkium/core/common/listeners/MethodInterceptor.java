package com.frameworkium.core.common.listeners;

import com.frameworkium.core.common.reporting.jira.api.SearchIssues;
import com.frameworkium.core.ui.driver.Driver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;

import java.lang.reflect.Method;
import java.util.List;

import static com.frameworkium.core.common.properties.Property.JIRA_URL;
import static com.frameworkium.core.common.properties.Property.JQL_QUERY;
import static com.frameworkium.core.ui.tests.BaseTest.getIssueOrTestCaseIdValue;
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
            logger.info("Filtering specified tests to run with JQL query results");

            List<IMethodInstance> methodsWithTestIDs = methodsToBeFiltered.stream()
                    .filter(m -> getIssueOrTestCaseIdValue(m).isPresent())
                    .collect(toList());

            List<String> testIDsFromJQL =
                    new SearchIssues(JQL_QUERY.getValue()).getKeys();

            List<IMethodInstance> methodsToRun = methodsWithTestIDs.stream()
                    .filter(m -> testIDsFromJQL
                            .contains(getIssueOrTestCaseIdValue(m)
                                    .orElseThrow(IllegalStateException::new)))
                    .collect(toList());

            logTestMethodInformation(
                    methodsToBeFiltered, methodsWithTestIDs, methodsToRun);

            return methodsToRun;
        } else {
            // Can't run the JQL without both JIRA_URL and JQL_QUERY
            return methodsToBeFiltered;
        }
    }

    // TODO - make this non-UI specific!
    private List<IMethodInstance> filterTestsToRunByDriverAndTestClassName(
            List<IMethodInstance> methods) {

        return methods.stream()
                .filter(instance -> {
                    String clazz = instance.getMethod().getRealClass().getName();

                    boolean appTest = clazz.endsWith("AppTest");
                    boolean mobiTest = clazz.endsWith("MobiTest");
                    boolean nonMobileNotAppMobiTest =
                            !Driver.isMobile() && !appTest && !mobiTest;
                    boolean nativeAppTest =
                            Driver.isNative() && appTest;
                    boolean nonNativeMobiTest =
                            !Driver.isNative() && mobiTest;

                    return nonMobileNotAppMobiTest || nativeAppTest || nonNativeMobiTest;
                })
                .collect(toList());
    }

    private void logTestMethodInformation(
            List<IMethodInstance> methodsPreFiltering,
            List<IMethodInstance> methodsWithTestIDs,
            List<IMethodInstance> methodsPostFiltering) {

        logger.debug("Running the following test methods:\n{}", () ->
                methodsPostFiltering.stream()
                        .map(m -> getMethodFromIMethod(m).getName())
                        .collect(joining("\n")));

        List<String> methodsWithoutTestIds = methodsPreFiltering.stream()
                .filter(m -> !methodsWithTestIDs.contains(m))
                .map(m -> getMethodFromIMethod(m).getName())
                .collect(toList());

        if (methodsWithoutTestIds.size() > 0) {
            logger.warn("The following tests don't have and TestIDs {}",
                    () -> methodsWithoutTestIds.stream().collect(joining(", ")));
        }

        logger.info("Running {} tests specified by JQL query...", methodsPostFiltering.size());
    }

    private Method getMethodFromIMethod(IMethodInstance iMethod) {
        return iMethod.getMethod().getConstructorOrMethod().getMethod();
    }
}
