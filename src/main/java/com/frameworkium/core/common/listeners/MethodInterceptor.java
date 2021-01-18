package com.frameworkium.core.common.listeners;

import com.frameworkium.core.common.reporting.TestIdUtils;
import com.frameworkium.core.common.reporting.jira.service.Search;
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

        if (!(JQL_QUERY.isSpecified() && JIRA_URL.isSpecified())) {
            // Can't run the JQL without both JIRA_URL and JQL_QUERY
            return methodsToBeFiltered;
        }

        logger.info("Filtering specified tests to run with JQL query results");

        List<String> testIDsFromJQL =
                new Search(JQL_QUERY.getValue()).getKeys();

        List<IMethodInstance> methodsToRun = methodsToBeFiltered.stream()
                .filter(m -> TestIdUtils.getIssueOrTmsLinkValue(m).isPresent())
                .filter(m -> testIDsFromJQL.contains(
                        TestIdUtils.getIssueOrTmsLinkValue(m).orElseThrow(IllegalStateException::new)))
                .collect(toList());

        logTestMethodInformation(methodsToRun);

        return methodsToRun;
    }

    private void logTestMethodInformation(List<IMethodInstance> methodsToRun) {

        logger.debug("Running the following test methods:\n{}", () ->
                methodsToRun.stream()
                        .map(m -> getMethodFromIMethod(m).getName())
                        .collect(joining("\n")));

        logger.info("Running {} tests specified by JQL query", methodsToRun.size());
    }

    private Method getMethodFromIMethod(IMethodInstance iMethod) {
        return iMethod.getMethod().getConstructorOrMethod().getMethod();
    }
}
