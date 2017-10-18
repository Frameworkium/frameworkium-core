package com.frameworkium.core.ui.truth

import org.openqa.selenium.WebElement
import spock.lang.Specification

class WebElementSubjectSpec extends Specification {

    def "element is displayed, passes isDisplayed() assertion"() {
        given:
            def webElement = Mock(WebElement)
        when:
            WebElementTruth.assertThat(webElement).isDisplayed()
        then:
            1 * webElement.isDisplayed() >> true
            noExceptionThrown()
    }

    def "element not displayed, isDisplayed() assertion throws Error"() {
        given:
            def webElement = Mock(WebElement)
        when:
            WebElementTruth.assertThat(webElement).isDisplayed()
        then:
            1 * webElement.isDisplayed() >> false
            def error = thrown(AssertionError)
            error.message ==~ /Not true that WebElement .* is displayed/
    }
}
