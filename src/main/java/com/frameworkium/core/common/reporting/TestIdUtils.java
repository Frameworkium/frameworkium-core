package com.frameworkium.core.common.reporting;

import org.testng.IMethodInstance;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.TestCaseId;

import java.lang.reflect.Method;
import java.util.Optional;

import static java.util.Objects.isNull;

public class TestIdUtils {

    private TestIdUtils() {
        // hide default constructor for this util class
    }

    /**
     * Get issue or test case ID.
     *
     * @param iMethod the {@link IMethodInstance} to check for test ID annotations.
     * @return Optional of either the {@link TestCaseId} or {@link Issue} value.
     * @throws IllegalStateException if {@link TestCaseId} and {@link Issue}
     *                               are specified inconstantly.
     */
    public static Optional<String> getIssueOrTestCaseIdValue(IMethodInstance iMethod) {
        Method method = iMethod.getMethod().getConstructorOrMethod().getMethod();
        return getIssueOrTestCaseIdValue(method);
    }

    /**
     * Get issue or test case ID for a method.
     *
     * @param method the method to check for test ID annotations.
     * @return Optional of the {@link TestCaseId} or {@link Issue} value.
     * @throws IllegalStateException if {@link TestCaseId} and {@link Issue}
     *                               are both specified inconstantly.
     */
    public static Optional<String> getIssueOrTestCaseIdValue(Method method) {
        TestCaseId tcIdAnnotation = method.getAnnotation(TestCaseId.class);
        Issue issueAnnotation = method.getAnnotation(Issue.class);

        if (!isNull(issueAnnotation) && !isNull(tcIdAnnotation)
                && !issueAnnotation.value().equals(tcIdAnnotation.value())) {
            throw new IllegalStateException(
                    "TestCaseId and Issue annotation are both specified but "
                            + "not equal for method: " + method.toString());
        }

        if (!isNull(issueAnnotation)) {
            return Optional.of(issueAnnotation.value());
        } else if (!isNull(tcIdAnnotation)) {
            return Optional.of(tcIdAnnotation.value());
        } else {
            return Optional.empty();
        }
    }
}
