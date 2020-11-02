package com.frameworkium.lite.ui.driver.lifecycle

import com.frameworkium.lite.ui.driver.Driver
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.events.EventFiringWebDriver
import spock.lang.Ignore
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
        then:
            sut.getWebDriver() == EFWebDriverMock

        when:
            sut.tearDownDriver()
            sut.tearDownDriverPool()
        then:
            1 * EFWebDriverMock.quit()
            noExceptionThrown()
    }

    def "following subset of expected lifecycle yields correct driver for SingleUseDriverLifecycle"() {
        when:
            sut.initBrowserBeforeTest(driverSupplier)
        then:
            sut.getWebDriver() == EFWebDriverMock
        when:
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

    def "reinit current driver quits existing and returns new"() {
        def EFWebDriverMock2 = Mock(constructorArgs: [webDriverStub], EventFiringWebDriver)
        def driverMock2 = Mock(Driver) {
            getWebDriver() >> EFWebDriverMock2
        }
        def driverSupplier2 = { driverMock2 }

        given:
            sut.initBrowserBeforeTest(driverSupplier)
            assert sut.getWebDriver() == EFWebDriverMock
        when:
            sut.reinitialiseCurrentDriver(driverSupplier2)
            sut.initBrowserBeforeTest(driverSupplier2)
        then:
            sut.getWebDriver() == EFWebDriverMock2
    }
}
