package com.frameworkium.core.ui.driver.lifecycle

import com.frameworkium.core.ui.driver.Driver
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.events.EventFiringWebDriver
import spock.lang.Specification

class SingleUseDriverLifecycleSpec extends Specification {

    def webDriverStub = Stub(WebDriver)
    def EFWebDriverMock =
            Mock(constructorArgs: [webDriverStub], EventFiringWebDriver) {
                getWrappedDriver() >> webDriverStub
            }
    def driverMock = Mock(Driver) {
        getWebDriver() >> EFWebDriverMock
    }
    def driverSupplier = { driverMock }

    def sut = new SingleUseDriverLifecycle()


    def "following expected lifecycle yields correct driver"() {
        when:
            sut.initDriverPool(driverSupplier)
            sut.initBrowserBeforeTest(driverSupplier)
            assert sut.getWebDriver() == webDriverStub
            sut.tearDownDriver()
            sut.tearDownDriverPool()
        then:
            1 * EFWebDriverMock.quit()
            noExceptionThrown()
    }

    def "following subset of expected lifecycle yields correct driver for SingleUseDriverLifecycle"() {
        when:
            sut.initBrowserBeforeTest(driverSupplier)
            assert sut.getWebDriver() == webDriverStub
            sut.tearDownDriver()
        then:
            1 * EFWebDriverMock.quit()
            noExceptionThrown()
    }

    def "if a driver fails to tearDown exception will be thrown"() {
        given:
            sut.initBrowserBeforeTest(driverSupplier)
        when:
            sut.tearDownDriver()
        then:
            1 * EFWebDriverMock.quit() >> { throw new Exception("") }
            thrown Exception
    }
}
