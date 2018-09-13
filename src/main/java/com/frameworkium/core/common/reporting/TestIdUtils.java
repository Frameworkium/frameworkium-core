package com.frameworkium.core.common.reporting;

import io.qameta.allure.Issue;
import io.qameta.allure.TmsLink;
import org.testng.IMethodInstance;

import java.lang.reflect.Method;
import java.util.Optional;

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
     */
    public static Optional<String> getIssueOrTmsLinkValue(IMethodInstance iMethod) {
        Method method = iMethod.getMethod().getConstructorOrMethod().getMethod();
        return getIssueOrTmsLinkValue(method);
    }

    /**
     * Get TMS Link or Issue ID for a method.
     *
     * @param method the method to check for test ID annotations.
     * @return Optional of the {@link TmsLink} or {@link Issue} value.
     * @throws IllegalStateException if {@link TmsLink} and {@link Issue}
     *                               are both specified inconstantly.
     */
    public static Optional<String> getIssueOrTmsLinkValue(Method method) {
        TmsLink tcIdAnnotation = method.getAnnotation(TmsLink.class);
        Issue issueAnnotation = method.getAnnotation(Issue.class);

        if (nonNull(issueAnnotation) && nonNull(tcIdAnnotation)
                && !issueAnnotation.value().equals(tcIdAnnotation.value())) {
            throw new IllegalStateException(
                    "TmsLink and Issue annotation are both specified but "
                            + "not equal for method: " + method);
        }

        if (nonNull(tcIdAnnotation)) {
            return Optional.of(tcIdAnnotation.value());
        } else if (nonNull(issueAnnotation)) {
            return Optional.of(issueAnnotation.value());
        } else {
            return Optional.empty();
        }
    }
}
