package com.frameworkium.core.common.retry

import org.testng.ITestResult
import spock.lang.Specification

class RetryFlakyTestSpec extends Specification {

    def "Retry will return true if there are retries remaining"() {
        given:
            def mockResult = Mock(ITestResult)
            def sut = new RetryFlakyTest() // assume default of 1
        expect:
            RetryFlakyTest.MAX_RETRY_COUNT.times { assert sut.retry(mockResult) }
            !sut.retry(mockResult)
    }
}
