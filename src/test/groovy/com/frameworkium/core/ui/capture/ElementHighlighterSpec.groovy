package com.frameworkium.core.ui.capture

import com.frameworkium.core.ui.driver.WebDriverWrapper
import org.openqa.selenium.*
import spock.lang.Specification

class ElementHighlighterSpec extends Specification {

    def mockWDWrapper = Mock(WebDriverWrapper, constructorArgs: [Mock(WebDriver)])
    def mockElement = Mock(WebElement)

    ElementHighlighter sut = new ElementHighlighter(mockWDWrapper)

    def "provided element is highlighted and same element is un-highlighted"() {
        given:
            def highlightJS = "arguments[0].style.border='3px solid red'"
            def unhighlightJS = "arguments[0].style.border='none'"
        when:
            sut.highlightElement(mockElement)
            sut.unhighlightPrevious()
        then:
            1 * mockWDWrapper.executeScript(highlightJS, mockElement)
            1 * mockWDWrapper.executeScript(unhighlightJS, mockElement)
    }

    def "StaleElementReferenceException's are caught"() {
        when:
            sut.highlightElement(mockElement)
            sut.unhighlightPrevious()
        then:
            1 * mockWDWrapper.executeScript(_ as String, mockElement) >> {
                throw new StaleElementReferenceException("")
            }
            1 * mockWDWrapper.executeScript(_ as String, mockElement) >> {
                throw new StaleElementReferenceException("")
            }
            notThrown(StaleElementReferenceException)
    }
}