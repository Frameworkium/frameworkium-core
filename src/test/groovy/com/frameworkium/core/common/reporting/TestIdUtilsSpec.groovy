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
            methodName      | expectedValue
            "none"          | Optional.empty()
            "tmsLink"       | Optional.of("TMSLink")
            "issue"         | Optional.of("ISSUE")
            "bothSame"      | Optional.of("SAME")
            "bothDifferent" | Optional.of("TMSLink")
    }

    class TestIdData {

        void none() {}

        @TmsLink("TMSLink")
        void tmsLink() {}

        @Issue("ISSUE")
        void issue() {}

        @TmsLink("SAME")
        @Issue("SAME")
        void bothSame() {}

        @TmsLink("TMSLink")
        @Issue("ISSUE")
        void bothDifferent() {}
    }
}

