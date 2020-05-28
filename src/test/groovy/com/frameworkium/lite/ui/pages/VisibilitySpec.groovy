package com.frameworkium.lite.ui.pages

import com.frameworkium.lite.ui.pages.pageobjects.PageObjects
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.events.EventFiringWebDriver
import org.openqa.selenium.support.ui.FluentWait
import org.openqa.selenium.support.ui.Sleeper
import org.openqa.selenium.support.ui.Wait
import com.frameworkium.lite.htmlelements.element.TextInput
import spock.lang.Specification

import java.time.Clock
import java.time.Duration

class VisibilitySpec extends Specification {

    /* Mocks used in each test */
    Wait<WebDriver> wait =
            new FluentWait<>(Stub(WebDriver), Clock.systemUTC(), Mock(Sleeper))
                    .pollingEvery(Duration.ofMillis(1))
                    // -1 prevents polling (because FluentWait uses `isBefore`)
                    .withTimeout(Duration.ofSeconds(-1))
    def mockJsEx = Mock(JavascriptExecutor)
    WebDriver mockDriver = Mock(EventFiringWebDriver, constructorArgs: [Mock(WebDriver)])

    def sut = new Visibility(wait, mockJsEx)

    // methods that create new Mocks each time

    // Assert exactly one isDisplayed() call per WebElement
    def newVisibleElement() {
        Mock(WebElement) { 1 * isDisplayed() >> true }
    }

    def newInvisibleElement() {
        Mock(WebElement) { 1 * isDisplayed() >> false }
    }

    def newNoInteractionElement() {
        Mock(WebElement) { 0 * isDisplayed() }
    }

    def newVisibleComponent() {
        def visibleComponent = new PageObjects.Component()
        visibleComponent.wrappedElement = newVisibleElement()
        visibleComponent.myVisibleWebElement = newVisibleElement()
        return visibleComponent
    }

    def "Wait for Single visible @Visible Element to be displayed"() {

        given: "A page object with @Visible element field"
            BasePage pageObject = new PageObjects.SingleVisibleElement(mockDriver, wait)

        when: "Waiting for a visible @Visible element"
            pageObject.visibleElement = newVisibleElement()
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "Wait is successful"
            notThrown(TimeoutException)
    }

    def "Wait for Single invisible @Visible Element to be displayed"() {

        given: "A page object with @Visible element field"
            BasePage pageObject = new PageObjects.SingleVisibleElement(mockDriver, wait)

        when: "Waiting for an invisible @Visible element"
            pageObject.visibleElement = newInvisibleElement()
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "Wait times out"
            thrown(TimeoutException)
    }

    def "Wait for Single invisible @Invisible Element to not be displayed"() {

        given: "A page object with @Invisible element field"
            def pageObject = new PageObjects.SingleInvisibleElement(mockDriver, wait)
            pageObject.invisibleTextInput = Mock(TextInput)

        when: "Waiting for an invisible @Invisible element"
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "Wait is successful when element is not displayed"
            1 * pageObject.invisibleTextInput.isDisplayed() >> false
            notThrown(Exception)
    }

    def "Wait for Single visible @Invisible Element to not be displayed"() {

        given: "A page object with @Invisible element field"
            def pageObject = new PageObjects.SingleInvisibleElement(mockDriver, wait)
            pageObject.invisibleTextInput = Mock(TextInput)

        when: "Waiting for a visible @Invisible element"
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "Wait times out if element is displayed"
            1 * pageObject.invisibleTextInput.isDisplayed() >> true
            thrown(TimeoutException)
    }

    def "Wait for Single visible @ForceVisible Element to be displayed"() {

        given: "A page object with @ForceVisible element field"
            def pageObject = new PageObjects.SingleForceVisibleElement(mockDriver, wait)

        when:
            pageObject.forceVisibleElement = newVisibleElement()
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "forces visible with JS"
            1 * mockJsEx.executeScript(_ as String, _ as WebElement)
        then: "wait is successful when element is displayed"
            notThrown(Exception)
    }

    def "Wait for Single invisible @ForceVisible Element to be displayed"() {

        given: "A page object with @ForceVisible element field"
            def pageObject = new PageObjects.SingleForceVisibleElement(mockDriver, wait)

        when:
            pageObject.forceVisibleElement = newInvisibleElement()
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "forces visible with JS"
            1 * mockJsEx.executeScript(_ as String, _ as WebElement)
        then: "wait times out when element is not displayed"
            thrown(TimeoutException)
    }

    def "Waiting for Lists of Elements passes as expected"() {

        given: "A page objects with Lists of Elements"
            def pageObject = new PageObjects.ListOfElements(mockDriver, wait)
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
            3 * mockJsEx.executeScript(_ as String, _ as WebElement)
    }

    def "Waiting for Lists of Elements passes as expected where checkAtMost=2"() {

        given: "A page objects with Lists of Elements"
            def pageObject = new PageObjects.ListOfElementsCheckAtMost(mockDriver, wait)
            pageObject.with {
                visibles = [newVisibleElement(), newVisibleElement(), newNoInteractionElement()]
                invisibles = [newInvisibleElement(), newInvisibleElement(), newNoInteractionElement()]
                forceVisibles = [newVisibleElement(), newVisibleElement(), newNoInteractionElement()]
            }
        when: "waiting for visibility"
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "No exceptions and 2 forceVisible JS executions"
            notThrown(Exception)
            2 * mockJsEx.executeScript(_ as String, _ as WebElement)
    }

    def "HtmlElement 'components' are treated as page objects"() {

        given: "A Page Object with a @Visible HtmlElement component"
            def pageObject = new PageObjects.VisibleComponent(mockDriver, wait)
            pageObject.visibleComponent = newVisibleComponent()
        when: "Waiting for @Visible of visible component and inner element"
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "No exceptions"
            notThrown(Exception)
    }

    def "SubClassed Page check all Visible tags in hierarchy"() {

        given:
            def subClassedComp = new PageObjects.SubClassedPage(mockDriver, wait)
            subClassedComp.visibleElement = newVisibleElement()
            subClassedComp.subClassedVisibleWebElement = newVisibleElement()
        when:
            sut.waitForAnnotatedElementVisibility(subClassedComp)
        then:
            notThrown(Exception)
    }

    def "List<HtmlElement> 'components' are treated as page objects"() {

        given:
            def pageObject = new PageObjects.VisibleComponents(mockDriver, wait)
            pageObject.visibleComponents = [newVisibleComponent(), newVisibleComponent()]
        when: "waiting for visibility"
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "no exceptions"
            notThrown(Exception)
    }

    def "more than one visibility annotation throws exception"() {

        given:
            def pageObject = new PageObjects.MultiVisibilityWebElement(mockDriver, wait)
            def pageObject2 = new PageObjects.MultiVisibilityTypifiedElement(mockDriver, wait)
        when:
            sut.waitForAnnotatedElementVisibility(pageObject)
        then: "Throws exception due to too many visibility related Annotations"
            def ex = thrown(IllegalArgumentException)
            ex.message ==~ /Field myWebElement on [A-z\\.$]+ has too many Visibility related Annotations/

        when:
            sut.waitForAnnotatedElementVisibility(pageObject2)
        then: "Throws exception due to too many visibility related Annotations"
            def ex2 = thrown(IllegalArgumentException)
            ex2.message ==~ /Field myTypifiedElement on [A-z\\.$]+ has too many Visibility related Annotations/
    }

    def "visibility annotations on invalid field type throws exception"() {

        given: "A Page Object with @Visible on invalid field type"
            def pageObject = new PageObjects.UnsupportedFieldType(mockDriver, wait)
        when:
            sut.waitForAnnotatedElementVisibility(pageObject)
        then:
            def ex = thrown(IllegalArgumentException)
            ex.message ==~ /Only .*HtmlElement, TypifiedElement, WebElement.*/
    }

}
