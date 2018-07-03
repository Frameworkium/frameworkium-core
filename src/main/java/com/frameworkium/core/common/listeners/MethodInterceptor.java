package com.frameworkium.core.common.listeners;

import com.frameworkium.core.common.reporting.TestIdUtils;
import com.frameworkium.core.common.reporting.jira.api.SearchIssues;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;

import java.lang.reflect.Method;
import java.util.List;

import static com.frameworkium.core.common.properties.Property.JIRA_URL;
import static com.frameworkium.core.common.properties.Property.JQL_QUERY;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class MethodInterceptor implements IMethodInterceptor {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public List<IMethodInstance> intercept(
            List<IMethodInstance> methods, ITestContext context) {

        return filterTestsToRunByJQL(methods);
    }

    private List<IMethodInstance> filterTestsToRunByJQL(
            List<IMethodInstance> methodsToBeFiltered) {

        if (JQL_QUERY.isSpecified() && JIRA_URL.isSpecified()) {
            logger.info("Filtering specified tests to run with JQL query results");

            List<IMethodInstance> methodsWithTestIDs = methodsToBeFiltered.stream()
                    .filter(m -> TestIdUtils.getIssueOrTmsLinkValue(m).isPresent())
                    .collect(toList());

            List<String> testIDsFromJQL =
                    new SearchIssues(JQL_QUERY.getValue()).getKeys();

            List<IMethodInstance> methodsToRun = methodsWithTestIDs.stream()
                    .filter(m -> testIDsFromJQL.contains(
                            TestIdUtils.getIssueOrTmsLinkValue(m).orElseThrow(IllegalStateException::new)))
                    .collect(toList());

            logTestMethodInformation(
                    methodsToBeFiltered, methodsWithTestIDs, methodsToRun);

            return methodsToRun;
        } else {
            // Can't run the JQL without both JIRA_URL and JQL_QUERY
            return methodsToBeFiltered;
        }
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
            logger.warn("The following tests don't have TestIDs {}",
                    () -> methodsWithoutTestIds.stream().collect(joining(", ")));
        }

        logger.info("Running {} tests specified by JQL query", methodsPostFiltering.size());
    }

    private Method getMethodFromIMethod(IMethodInstance iMethod) {
        return iMethod.getMethod().getConstructorOrMethod().getMethod();
    }
}
