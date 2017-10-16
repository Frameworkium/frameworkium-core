package com.frameworkium.core.ui.truth

import org.openqa.selenium.WebElement
import spock.lang.Specification

class WebElementSubjectSpec extends Specification {

    def "displayed element passes is displayed assertion"() {
        given:
            def webElement = Mock(WebElement)
        when:
            WebElementTruth.assertThat(webElement).isDisplayed()
        then:
            1 * webElement.isDisplayed() >> true
            noExceptionThrown()
    }

    def "not displayed element fails is displayed assertion"() {
        given:
            def webElement = Mock(WebElement)
        when:
            WebElementTruth.assertThat(webElement).isDisplayed()
        then:
            1 * webElement.isDisplayed() >> false
            def error = thrown(AssertionError)
            error.message == "Not true that WebElement (<Mock for type 'WebElement' named 'webElement'>) is displayed"
    }
}
