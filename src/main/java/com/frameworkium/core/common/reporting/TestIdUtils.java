package com.frameworkium.core.common.reporting;

import io.qameta.allure.Issue;
import io.qameta.allure.TmsLink;
import org.testng.IMethodInstance;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

public class TestIdUtils {

    private TestIdUtils() {
        // hide default constructor for this util class
    }

    /**
     * Get TMS Link or Issue ID value.
     *
     * @param iMethod the {@link IMethodInstance} to check for test ID annotations.
     * @return Optional of either the {@link TmsLink} or {@link Issue} value.
     * @throws IllegalStateException if {@link TmsLink} and {@link Issue}
     *                               are specified inconstantly.
     * @deprecated Use {@link com.frameworkium.core.common.reporting.TestIdUtils#getIssueOrTmsLinkValues(IMethodInstance) instead}
     */
    @Deprecated
    public static Optional<String> getIssueOrTmsLinkValue(IMethodInstance iMethod) {
        Method method = iMethod.getMethod().getConstructorOrMethod().getMethod();
        return getIssueOrTmsLinkValue(method);
    }

    /**
     * Get list of {@link TmsLink} or {@link Issue}.
     *
     * @param iMethod the {@link IMethodInstance} to check for test ID annotations.
     * @return List of either the {@link TmsLink} or {@link Issue} value.
     * @throws IllegalStateException if {@link TmsLink} and {@link Issue}
     *                               are specified inconstantly.
     */
    public static List<String> getIssueOrTmsLinkValues(IMethodInstance iMethod) {
        Method method = iMethod.getMethod().getConstructorOrMethod().getMethod();
        return getIssueOrTmsLinkValues(method);
    }

    /**
     * Get {@link TmsLink} or {@link Issue} for a method.
     * If both are specified it will return jus the {@link TmsLink} value.
     *
     * @param method the method to check for test ID annotations.
     * @return Optional of the {@link TmsLink} or {@link Issue} value.
     * @deprecated Use {@link com.frameworkium.core.common.reporting.TestIdUtils#getIssueOrTmsLinkValues(Method) instead}
     */
    @Deprecated
    public static Optional<String> getIssueOrTmsLinkValue(Method method) {
        TmsLink tcIdAnnotation = method.getAnnotation(TmsLink.class);
        Issue issueAnnotation = method.getAnnotation(Issue.class);

        if (nonNull(tcIdAnnotation)) {
            return Optional.of(tcIdAnnotation.value());
        } else if (nonNull(issueAnnotation)) {
            return Optional.of(issueAnnotation.value());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Get a list of {@link TmsLink} or {@link Issue} for a method.
     * If both are specified it will return just the list of {@link TmsLink} values.
     *
     * @param method the method to check for test Id annotations.
     * @return List of {@link TmsLink} or {@link Issue} values.
     */
    public static List<String> getIssueOrTmsLinkValues(Method method) {
        TmsLink[] tcIdAnnotations = method.getAnnotationsByType(TmsLink.class);
        Issue[] issueAnnotations = method.getAnnotationsByType(Issue.class);
        if (tcIdAnnotations.length > 0) {
            return Stream.of(tcIdAnnotations).map(TmsLink::value).collect(Collectors.toList());
        }
        if (issueAnnotations.length > 0) {
            return Stream.of(issueAnnotations).map(Issue::value).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
