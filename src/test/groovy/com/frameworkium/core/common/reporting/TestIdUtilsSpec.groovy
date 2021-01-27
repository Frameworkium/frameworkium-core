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
            methodName                | expectedValue
            "none"                    | Optional.empty()
            "tmsLink"                 | Optional.of("TMSLink")
            "issue"                   | Optional.of("ISSUE")
            "bothSame"                | Optional.of("SAME")
            "bothDifferent"           | Optional.of("TMSLink")
            "multipleTmsLink"         | Optional.empty()
            "multipleTmsLinkAndIssue" | Optional.empty()
            "multipleIssue"           | Optional.empty()
    }

    def "GetIssueOrTmsLinkValues get TmsLink or Issue values for method #methodName"() {
        when:
            def value = TestIdUtils.getIssueOrTmsLinkValues(
                    TestIdData.getMethod(methodName))
        then:
            value == expectedValue
        where:
            methodName                | expectedValue
            "none"                    | []
            "tmsLink"                 | ["TMSLink"]
            "issue"                   | ["ISSUE"]
            "bothSame"                | ["SAME"]
            "bothDifferent"           | ["TMSLink"]
            "multipleTmsLink"         | ["TMSLink1", "TMSLink2"]
            "multipleTmsLinkAndIssue" | ["TMSLink3", "TMSLink4"]
            "multipleIssue"           | ["Issue3", "Issue4"]
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

        @TmsLink("TMSLink1")
        @TmsLink("TMSLink2")
        void multipleTmsLink() {}

        @Issue("Issue1")
        @Issue("Issue2")
        @TmsLink("TMSLink3")
        @TmsLink("TMSLink4")
        void multipleTmsLinkAndIssue() {}

        @Issue("Issue3")
        @Issue("Issue4")
        void multipleIssue() {}
    }
}

