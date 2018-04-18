package com.frameworkium.core.common.reporting

import io.qameta.allure.Issue
import io.qameta.allure.TmsLink
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class TestIdUtilsSpec extends Specification {

    def "GetIssueOrTmsLinkValue gets TmsLink or Issue value for method #methodName"() {
        when:
            def value = TestIdUtils.getIssueOrTmsLinkValue(
                    TestIdData.getMethod(methodName))
        then:
            value == expectedValue
        where:
            methodName | expectedValue
            "none"     | Optional.empty()
            "tmsLink"  | Optional.of("TC-ID")
            "issue"    | Optional.of("ISSUE")
            "bothSame" | Optional.of("SAME")
    }

    def "GetIssueOrTmsLinkValue errors if the TmsLink and Issue are specified with different values"() {
        when:
            TestIdUtils.getIssueOrTmsLinkValue(TestIdData.getMethod("bothDifferent"))
        then:
            thrown(IllegalStateException)
    }

    class TestIdData {

        public void none() {}

        @TmsLink("TC-ID")
        public void tmsLink() {}

        @Issue("ISSUE")
        public void issue() {}

        @TmsLink("SAME")
        @Issue("SAME")
        public void bothSame() {}

        @TmsLink("SAME-SAME")
        @Issue("BUT DIFFERENT")
        public void bothDifferent() {}

    }
}

