package com.frameworkium.core.common.retry

import org.testng.ITestResult
import spock.lang.Specification

class RetryFlakyTestSpec extends Specification {

    def "Retry will return true if called when the number of retires left is greater than times called"() {
        given:
            def mockResult = Mock(ITestResult)
            def sut = new RetryFlakyTest() // assume default of 1
        when:
            def first = sut.retry(mockResult)
            def second = sut.retry(mockResult)
        then:
            first
            !second
    }
}
