package com.frameworkium.core.common.reporting

import ru.yandex.qatools.allure.annotations.Issue
import ru.yandex.qatools.allure.annotations.TestCaseId
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class TestIdUtilsSpec extends Specification {

    def "GetIssueOrTestCaseIdValue gets TestCaseId or Issue value for method #methodName"() {
        when:
            def value = TestIdUtils.getIssueOrTestCaseIdValue(
                    TestIdData.getMethod(methodName))
        then:
            value == expectedValue
        where:
            methodName   | expectedValue
            "none"       | Optional.empty()
            "testCaseID" | Optional.of("TC-ID")
            "issue"      | Optional.of("ISSUE")
            "bothSame"   | Optional.of("SAME")
    }

    def "GetIssueOrTestCaseIdValue errors if the TestCaseId and Issue are specified with different values"() {
        when:
            TestIdUtils.getIssueOrTestCaseIdValue(TestIdData.getMethod("bothDifferent"))
        then:
            thrown(IllegalStateException)
    }

    class TestIdData {

        public void none() {}

        @TestCaseId("TC-ID")
        public void testCaseID() {}

        @Issue("ISSUE")
        public void issue() {}

        @TestCaseId("SAME")
        @Issue("SAME")
        public void bothSame() {}

        @TestCaseId("SAME-SAME")
        @Issue("BUT DIFFERENT")
        public void bothDifferent() {}

    }
}

