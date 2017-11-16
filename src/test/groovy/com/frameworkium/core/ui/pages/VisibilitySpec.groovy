package com.frameworkium.core.ui.pages

import com.frameworkium.core.ui.driver.Driver
import com.frameworkium.core.ui.pages.pageobjects.PageObjects
import com.frameworkium.core.ui.tests.BaseTest
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

    /* Mocks used in each test */
    def wait = new FluentWait<>(Mock(WebDriver), Mock(Clock), Mock(Sleeper))
    def mockDriver = Mock(JavascriptExecutor)
    def sut = new Visibility(wait, mockDriver)

    // methods which create new Mocks each time

    // Assert exactly one isDisplayed() call per WebElement
    def newVisibleElement() {
        return Mock(WebElement) { 1 * isDisplayed() >> true }
    }

    def newInvisibleElement() {
        return Mock(WebElement) { 1 * isDisplayed() >> false }
    }

    def newNoInteractionElement(){
        return Mock(WebElement) { 0 * isDisplayed()}
    }

    def newVisibleComponent() {
        def visibleComponent = new PageObjects.Component()
        visibleComponent.wrappedElement = newVisibleElement()
        visibleComponent.myVisibleWebElement = newVisibleElement()
        return visibleComponent
    }

    def setup() {
        BaseTest.setDriver(Mock(Driver))
        BaseTest.setWait(wait)
    }

    def "Wait for Single @Visible Element to be displayed"() {

        given: "A page object with @Visible element field"
            def pageObject = new PageObjects.SingleVisibleElement()

        when: "Waiting for a visible @Visible element"
            pageObject.visibleElement = newVisibleElement()
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "Wait is successful"
            notThrown(TimeoutException)

        when: "Waiting for an invisible @Visible element"
            pageObject.visibleElement = newInvisibleElement()
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
            1 * pageObject.invisibleTextInput.isDisplayed() >> false
            notThrown(Exception)

        when: "Waiting for a visible @Invisible element"
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "Wait times out if element is displayed"
            1 * pageObject.invisibleTextInput.isDisplayed() >> true
            thrown(TimeoutException)
    }

    def "Wait for Single @ForceVisible Element to be displayed"() {

        given: "A page object with @ForceVisible element field"
            def pageObject = new PageObjects.SingleForceVisibleElement()

        when:
            pageObject.forceVisibleElement = newVisibleElement()
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "forces visible with JS"
            1 * mockDriver.executeScript(_ as String, _ as WebElement)
        then: "wait is successful when element is displayed"
            notThrown(Exception)

        when:
            pageObject.forceVisibleElement = newInvisibleElement()
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "forces visible with JS"
            1 * mockDriver.executeScript(_ as String, _ as WebElement)
        then: "wait times out when element is not displayed"
            thrown(TimeoutException)
    }

    def "Waiting for Lists of Elements passes as expected"() {

        given: "A page objects with Lists of Elements"
            def pageObject = new PageObjects.ListOfElements()
            pageObject.with {
                visibles = [newVisibleElement()]
                invisibles = [newInvisibleElement(), newInvisibleElement()]
                emptyInvisible = []
                forceVisibles = [newVisibleElement(), newVisibleElement(), newVisibleElement()]
            }
        when: "waiting for visibility"
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "No exceptions and 3 forceVisible JS executions"
            notThrown(Exception)
            3 * mockDriver.executeScript(_ as String, _ as WebElement)
    }

    def "Waiting for Lists of Elements passes as expected where atLeast=2"() {

        given: "A page objects with Lists of Elements"
        def pageObject = new PageObjects.ListOfElementsWithAtLeast()
            pageObject.with {
                visibles = [newVisibleElement(), newVisibleElement(), newNoInteractionElement()]
                invisibles = [newInvisibleElement(), newInvisibleElement(), newNoInteractionElement()]
                forceVisibles = [newVisibleElement(), newVisibleElement(), newNoInteractionElement()]
            }
        when: "waiting for visibility"
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "No exceptions and 2 forceVisible JS executions"
            notThrown(Exception)
            2 * mockDriver.executeScript(_ as String, _ as WebElement)
    }

    def "HtmlElement 'components' are treated as page objects"() {

        given: "A Page Object with a @Visible HtmlElement component"
            def pageObject = new PageObjects.VisibleComponent()
            pageObject.visibleComponent = newVisibleComponent()
        when: "Waiting for @Visible of visible component and inner element"
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "No exceptions"
            notThrown(Exception)
    }

    def "SubClassed Page check all Visible tags in hierarchy"() {

        given:
            def subClassedComp = new PageObjects.SubClassedPage()
            subClassedComp.visibleElement = newVisibleElement()
            subClassedComp.subClassedVisibleWebElement = newVisibleElement()
        when:
            sut.waitForAnnotatedElementVisibility(subClassedComp)
        then:
            notThrown(Exception)
    }

    def "List<HtmlElement> 'components' are treated as page objects"() {

        given:
            def pageObject = new PageObjects.VisibleComponents()
            pageObject.visibleComponents = [newVisibleComponent(), newVisibleComponent()]
        when: "waiting for visibility"
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "no exceptions"
            notThrown(Exception)
    }

    def "more than one visibility annotation throws exception"() {

        when:
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "Throws exception due to too many visibility related Annotations"
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
