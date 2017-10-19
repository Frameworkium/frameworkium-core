package com.frameworkium.core.ui

import com.frameworkium.core.ui.driver.WebDriverWrapper
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.Clock
import org.openqa.selenium.support.ui.FluentWait
import org.openqa.selenium.support.ui.Sleeper
import spock.lang.Specification

class ExtraExpectedConditionsSpec extends Specification {

    def mockElement = Mock(WebElement)
    def wait = new FluentWait<>(Mock(WebDriver), Mock(Clock), Mock(Sleeper))

    // notPresentOrInvisible(WebElement)

    def "waiting for hidden element to be notPresentOrInvisible passes"() {
        given: "The element is not displayed"
            mockElement.isDisplayed() >> false >> {
                throw new NoSuchElementException("")
            }
        when: "waiting for the item not to be present or invisible"
            wait.until(ExtraExpectedConditions.notPresentOrInvisible(mockElement))
            wait.until(ExtraExpectedConditions.notPresentOrInvisible(mockElement))
        then: "nothing is thrown"
            noExceptionThrown()
    }

    def "waiting for visible element notPresentOrInvisible fails"() {
        given: "The element is displayed"
            mockElement.isDisplayed() >> true
        when: "waiting for the item not to be present or invisible"
            wait.until(ExtraExpectedConditions.notPresentOrInvisible(mockElement))
        then: "a timeout exception is thrown"
            thrown(TimeoutException)
    }

    // notPresentOrInvisible(List<? extends WebElement>)

    def "waiting for hidden or not present elements to be notPresentOrInvisible passes"() {
        given: "The elements are not displayed"
            mockElement.isDisplayed() >> false
        when: "waiting for the items not to be present or invisible"
            wait.until(ExtraExpectedConditions.notPresentOrInvisible(webElements))
            wait.until(ExtraExpectedConditions.notPresentOrInvisible([]))
        then: "nothing is thrown"
            noExceptionThrown()
    }

    def "waiting for visible elements notPresentOrInvisible fails"() {
        given: "The elements are displayed"
            mockElement.isDisplayed() >> true
        when: "waiting for the items not to be present or invisible"
            wait.until(ExtraExpectedConditions.notPresentOrInvisible([mockElement] * 2))
        then: "a timeout exception is thrown"
            thrown(TimeoutException)
    }

    // jQueryAjaxDone()
    def "waiting for jQueryAjaxDone runs some assumed correct JavaScript"() {
        given: "A driver which can be cast to JavascriptExecutor"
            def mockWDWrapper = Mock(WebDriverWrapper, constructorArgs: [Mock(WebDriver)])
            def jsWait = new FluentWait<>(mockWDWrapper, Mock(Clock), Mock(Sleeper))
        when: "Waiting for jQuery ajax"
            jsWait.until(ExtraExpectedConditions.jQueryAjaxDone())
        then: "nothing is thrown if ajax is finished"
            1 * mockWDWrapper.executeScript(_ as String) >> true
            noExceptionThrown()

        when: "Waiting for jQuery ajax"
            jsWait.until(ExtraExpectedConditions.jQueryAjaxDone())
        then: "Timeout is thrown if ajax is not finished"
            1 * mockWDWrapper.executeScript(_ as String) >> false
            thrown(TimeoutException)
    }

    // documentBodyReady()

    def "waiting for documentBodyReady runs some assumed correct JavaScript"() {
        given: "A driver which can be cast to JavascriptExecutor"
            def mockWDWrapper = Mock(WebDriverWrapper, constructorArgs: [Mock(WebDriver)])
            def jsWait = new FluentWait<>(mockWDWrapper, Mock(Clock), Mock(Sleeper))
        when: "Waiting for document body ready"
            jsWait.until(ExtraExpectedConditions.documentBodyReady())
        then: "nothing is thrown if body is ready is finish"
            1 * mockWDWrapper.executeScript(_ as String) >> true
            noExceptionThrown()

        when: "Waiting for documentBodyReady"
            jsWait.until(ExtraExpectedConditions.documentBodyReady())
        then: "Timeout is thrown if body is not ready"
            1 * mockWDWrapper.executeScript(_ as String) >> false
            thrown(TimeoutException)
    }

    def static listSize = 3
    def webElements = [mockElement] * listSize

    // sizeGreaterThan(List<? extends WebElement>, int)

    def "waiting for list size to be greater than a number less than its size passes"() {
        when: "waiting for the list size to be greater than its size"
            wait.until(ExtraExpectedConditions.sizeGreaterThan(webElements, listSize - 1))
        then: "nothing is thrown"
            noExceptionThrown()
    }

    def "waiting for list size to be greater than a number greater or equal than its size fails"() {
        when: "waiting for the list size to be greater than or equal its size"
            wait.until(ExtraExpectedConditions.sizeGreaterThan(webElements, size))
        then: "a timeout exception is thrown"
            thrown(TimeoutException)
        where:
            size << [listSize, listSize + 1]
    }

    // sizeLessThan(List<? extends WebElement>, int)

    def "waiting for list size to be less than a number greater than its size passes"() {
        when: "waiting for the list size to be less than its size"
            wait.until(ExtraExpectedConditions.sizeLessThan(webElements, listSize + 1))
        then: "nothing is thrown"
            noExceptionThrown()
    }

    def "waiting for list size to be less than a number less or equal to its size fails"() {
        when: "waiting for the list size to be less than or equal to its size"
            wait.until(ExtraExpectedConditions.sizeLessThan(webElements, size))
        then: "a timeout exception is thrown"
            thrown(TimeoutException)
        where:
            size << [listSize, listSize - 1]
    }
}
