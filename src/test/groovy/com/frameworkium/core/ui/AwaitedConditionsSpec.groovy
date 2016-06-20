package com.frameworkium.core.ui

import org.openqa.selenium.*
import org.openqa.selenium.support.ui.*
import spock.lang.Specification

class AwaitedConditionsSpec extends Specification {

    def mockElement = Mock(WebElement)
    def wait = new FluentWait<>(Mock(WebDriver), Mock(Clock), Mock(Sleeper))

    // notPresentOrInvisible(WebElement)

    def "waiting for hidden element to be notPresentOrInvisible passes"() {
        given: "The element is not displayed"
            mockElement.isDisplayed() >> false >> { throw new NoSuchElementException("") }
        when: "waiting for the item not to be present or invisible"
            wait.until(AwaitedConditions.notPresentOrInvisible(mockElement))
            wait.until(AwaitedConditions.notPresentOrInvisible(mockElement))
        then: "nothing is thrown"
            noExceptionThrown()
    }

    def "waiting for visible element notPresentOrInvisible fails"() {
        given: "The element is displayed"
            mockElement.isDisplayed() >> true
        when: "waiting for the item not to be present or invisible"
            wait.until(AwaitedConditions.notPresentOrInvisible(mockElement))
        then: "a timeout exception is thrown"
            thrown(TimeoutException)
    }

    // notPresentOrInvisible(List<? extends WebElement>)

    def "waiting for hidden or not present elements to be notPresentOrInvisible passes"() {
        given: "The elements are not displayed"
            mockElement.isDisplayed() >> false
        when: "waiting for the items not to be present or invisible"
            wait.until(AwaitedConditions.notPresentOrInvisible(webElements))
            wait.until(AwaitedConditions.notPresentOrInvisible([]))
        then: "nothing is thrown"
            noExceptionThrown()
    }

    def "waiting for visible elements notPresentOrInvisible fails"() {
        given: "The elements are displayed"
            mockElement.isDisplayed() >> true
        when: "waiting for the items not to be present or invisible"
            wait.until(AwaitedConditions.notPresentOrInvisible([mockElement] * 2))
        then: "a timeout exception is thrown"
            thrown(TimeoutException)
    }

    // jQueryAjaxDone not testable due to cast driver to JavascriptExecutor
    // ...

    def static listSize = 3
    def webElements = [mockElement] * listSize

    // sizeGreaterThan(List<? extends WebElement>, int)

    def "waiting for list size to be greater than a number less than its size passes"() {
        when: "waiting for the list size to be greater than its size"
            wait.until(AwaitedConditions.sizeGreaterThan(webElements, listSize - 1))
        then: "nothing is thrown"
            noExceptionThrown()
    }

    def "waiting for list size to be greater than a number greater or equal than its size fails"() {
        when: "waiting for the list size to be greater than or equal its size"
            wait.until(AwaitedConditions.sizeGreaterThan(webElements, size))
        then: "a timeout exception is thrown"
            thrown(TimeoutException)
        where:
            size << [listSize, listSize + 1]
    }

    // sizeLessThan(List<? extends WebElement>, int)

    def "waiting for list size to be less than a number greater than its size passes"() {
        when: "waiting for the list size to be less than its size"
            wait.until(AwaitedConditions.sizeLessThan(webElements, listSize + 1))
        then: "nothing is thrown"
            noExceptionThrown()
    }

    def "waiting for list size to be less than a number less or equal to its size fails"() {
        when: "waiting for the list size to be less than or equal to its size"
            wait.until(AwaitedConditions.sizeLessThan(webElements, size))
        then: "a timeout exception is thrown"
            thrown(TimeoutException)
        where:
            size << [listSize, listSize - 1]
    }
}
