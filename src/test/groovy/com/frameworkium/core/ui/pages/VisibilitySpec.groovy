package com.frameworkium.core.ui.pages

import com.frameworkium.core.ui.pages.pageobjects.PageObjects
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.Clock
import org.openqa.selenium.support.ui.FluentWait
import org.openqa.selenium.support.ui.Sleeper
import ru.yandex.qatools.htmlelements.element.TextInput
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class VisibilitySpec extends Specification {

    def wait = new FluentWait<>(Mock(WebDriver), Mock(Clock), Mock(Sleeper))
    def mockDriver = Mock(JavascriptExecutor)

    def visibleElement = Mock(WebElement) {
        isDisplayed() >> true
    }
    def invisibleElement = Mock(WebElement) {
        isDisplayed() >> false
    }

    def sut = new Visibility(wait, mockDriver)

    def "Wait for Single @Visible Element to be displayed"() {

        given: "A page object with @Visible element field"
            def pageObject = new PageObjects.SingleVisibleElement()

        when: "Waiting for a visible @Visible element"
            pageObject.visibleElement = visibleElement
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "Wait is successful"
            notThrown(TimeoutException)

        when: "Waiting for an invisible @Visible element"
            pageObject.visibleElement = invisibleElement
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "Wait times out"
            thrown(TimeoutException)
    }

    def "Wait for Single @Invisible Element to not be displayed"() {

        given: "A page object with @Invisible element field"
            def pageObject = new PageObjects.SingleInvisibleElement()
            pageObject.invisibleTextInput = Mock(TextInput)

        when: "Waiting for an invisible @Invisible element"
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "Wait is successful when element is not displayed"
            pageObject.invisibleTextInput.isDisplayed() >> false
            notThrown(TimeoutException)

        when: "Waiting for a visible @Invisible element"
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "Wait times out if element is displayed"
            pageObject.invisibleTextInput.isDisplayed() >> true
            thrown(TimeoutException)
    }

    def "Wait for Single @ForceVisible Element to be displayed"() {

        given: "A page object with @ForceVisible element field"
            def pageObject = new PageObjects.SingleForceVisibleElement()
            def e = Mock(WebElement)
            pageObject.forceVisibleElement = e

        when:
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "forces visible with JS"
            1 * mockDriver.executeScript(_ as String, e)
        then: "wait is successful when element is displayed"
            e.isDisplayed() >> true
            notThrown(TimeoutException)

        when:
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "forces visible with JS"
            1 * mockDriver.executeScript(_ as String, e)
        then: "wait times out when element is not displayed"
            e.isDisplayed() >> false
            thrown(TimeoutException)
    }

    def "Waiting for Lists of Elements passes as expected"() {

        given: "A page objects with Lists of Elements"
            def pageObject = new PageObjects.ListOfElements()
            def visibleElements = [visibleElement] * 3
            def invisibleElements = [invisibleElement] * 4

        when: "waiting for visibility"
            pageObject.with {
                visibles = visibleElements
                invisibles = invisibleElements
                forceVisibles = visibleElements
            }
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "No exceptions and also a script call per @ForceVisible element"
            notThrown(TimeoutException)
            3 * mockDriver.executeScript(_ as String, visibleElement)
    }

    def "HtmlElement 'components' are treated as page objects"() {

        given: "A Page Object with a @Visible HtmlElement component"
            def pageObject = new PageObjects.VisibleComponent()
            pageObject.visibleComponent = createVisibleComponent()
        when: "Waiting for @Visible of visible component and inner element"
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "No exceptions"
            notThrown(TimeoutException)
    }

    def "List<HtmlElement> 'components' are treated as page objects"() {

        given:
            PageObjects.Component visibleComponent = createVisibleComponent()
            def pageObject = new PageObjects.VisibleComponents()
            pageObject.visibleComponents = [visibleComponent] * 3

        when: "waiting for visibility"
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "no exceptions"
            notThrown(TimeoutException)
    }

    def createVisibleComponent() {
        def visibleElement = Mock(WebElement) {
            isDisplayed() >> true
        }
        def visibleComponent = new PageObjects.Component()
        visibleComponent.wrappedElement = visibleElement
        visibleComponent.myVisibleWebElement = visibleElement
        return visibleComponent
    }

    def "more than one visibility annotation throws exception"() {
        when:
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "Throws exception due to too many Visibility related Annotations"
            def ex = thrown(IllegalArgumentException)
            ex.message ==~
                    /Field $element on [A-z\.$]+ has too many Visibility related Annotations/
        where:
            pageObject                                       || element
            new PageObjects.MultiVisibilityWebElement()      || "myWebElement"
            new PageObjects.MultiVisibilityTypifiedElement() || "myTypifiedElement"
    }

    def "visibility annotations on invalid field type throws exception"() {
        given: "A Page Object with @Visible on invalid field type"
            def pageObject = new PageObjects.UnsupportedFieldType()
        when:
            sut.waitForAnnotatedElementVisibility(pageObject)
        then:
            def ex = thrown(IllegalArgumentException)
            ex.message ==~ /Only .*HtmlElement, TypifiedElement, WebElement.*/
    }

}
