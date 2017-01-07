package com.frameworkium.core.ui.pages

import com.frameworkium.core.ui.driver.Driver
import com.frameworkium.core.ui.driver.WebDriverWrapper
import com.frameworkium.core.ui.pages.pageobjects.TestPage
import com.frameworkium.core.ui.tests.BaseTest
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.Clock
import org.openqa.selenium.support.ui.FluentWait
import org.openqa.selenium.support.ui.Sleeper
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class PageFactorySpec extends Specification {

    def mockDriver = Mock(Driver)
    def webDriverWrapperSpy = Spy(WebDriverWrapper, constructorArgs: [Mock(RemoteWebDriver)])
    def mockWait = Mock(FluentWait, constructorArgs: [webDriverWrapperSpy, Mock(Clock), Mock(Sleeper)])

    def "Instantiate a simple page object"() {

        given: "A driver which will load a simple page"
            BaseTest.setDriver(mockDriver)
            BaseTest.setWait(mockWait)

        when: "Asking for a new instance of a simple page"
            def pageObject = PageFactory.newInstance(TestPage.class)

        then: "Wait is successful"
            2 * mockDriver.getDriver() >> webDriverWrapperSpy
            1 * webDriverWrapperSpy.executeScript(_ as String) >> true
            !pageObject.isWebElementNull()
    }

}
