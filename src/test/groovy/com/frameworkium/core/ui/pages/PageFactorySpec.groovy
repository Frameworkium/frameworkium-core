package com.frameworkium.core.ui.pages

import com.frameworkium.core.ui.capture.ScreenshotCapture
import com.frameworkium.core.ui.driver.Driver
import com.frameworkium.core.ui.driver.WebDriverWrapper
import com.frameworkium.core.ui.pages.pageobjects.TestPage
import com.frameworkium.core.ui.tests.BaseUITest
import org.junit.Assume
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.FluentWait
import org.openqa.selenium.support.ui.Sleeper
import spock.lang.Specification

import java.time.Clock

class PageFactorySpec extends Specification {

    def mockDriver = Mock(Driver)
    def webDriverWrapperSpy = Spy(WebDriverWrapper, constructorArgs: [Mock(RemoteWebDriver)])
    def mockWait = Mock(FluentWait, constructorArgs: [webDriverWrapperSpy, Clock.systemUTC(), Mock(Sleeper)])


    def "Instantiate a simple page object"() {
        // skip test if capture is enabled otherwise things break!
        Assume.assumeFalse(ScreenshotCapture.isRequired())

        given: "A driver which will load a simple page"
            BaseUITest.setDriver(mockDriver)
            BaseUITest.setWait(mockWait)

        when: "Asking for a new instance of a simple page"
            def pageObject = PageFactory.newInstance(TestPage.class)

        then: "Wait is successful"
            2 * mockDriver.getDriver() >> webDriverWrapperSpy
            1 * webDriverWrapperSpy.executeScript(_ as String) >> true
            !pageObject.isWebElementNull()
    }

}
