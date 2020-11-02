package com.frameworkium.lite.ui.driver.lifecycle

import com.frameworkium.lite.ui.driver.Driver
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.events.EventFiringWebDriver
import spock.lang.Specification

class MultiUseDriverLifecycleSpec extends Specification {

    def webDriverStub = Mock(WebDriver)
    def EFWebDriverMock =
            Mock(constructorArgs: [webDriverStub], EventFiringWebDriver) {
                getWrappedDriver() >> webDriverStub
            }
    def driverMock = Mock(Driver) {
        getWebDriver() >> EFWebDriverMock
    }
    def driverSupplier = { driverMock }

    def "following expected lifecycle yields correct driver with MultiUseDriverLifecycle(#poolSize)"() {
        given:
            def sut = new MultiUseDriverLifecycle(poolSize)
        when:
            sut.initDriverPool(driverSupplier)
            sut.initBrowserBeforeTest(driverSupplier)
        then:
            sut.getWebDriver() == EFWebDriverMock

        when:
            sut.tearDownDriver()
            sut.tearDownDriverPool()
        then:
            EFWebDriverMock.manage() >> Stub(WebDriver.Options)
            poolSize * EFWebDriverMock.quit()
            noExceptionThrown()
        where:
            poolSize << [1, 5]
    }

    def "if a driver fails to tearDown exception will be thrown"() {
        given:
            def sut = new MultiUseDriverLifecycle(1)
            sut.initDriverPool(driverSupplier)
            sut.initBrowserBeforeTest(driverSupplier)
        when:
            sut.tearDownDriver()
        then:
            1 * EFWebDriverMock.manage() >> { throw new Exception("") }
            thrown Exception
    }

    def "throwing exception on quit does not prevent subsequent drivers quitting"() {
        given:
            def sut = new MultiUseDriverLifecycle(2)
            sut.initDriverPool(driverSupplier)
        when:
            sut.tearDownDriverPool()
        then:
            // throw one error then do nothing for subsequent calls
            2 * EFWebDriverMock.quit() >>> {
                throw new Exception("some error")
            } >> {}
    }

    def "initDriverPool can only be called once"() {
        given:
            def sut = new MultiUseDriverLifecycle(1)
            sut.initDriverPool(driverSupplier)
        when:
            sut.initDriverPool(driverSupplier)
        then:
            thrown IllegalStateException
    }

    def "initDriverPool can only be called again after tearDownDriverPool"() {
        given:
            def sut = new MultiUseDriverLifecycle(1)
            sut.initDriverPool(driverSupplier)
            sut.tearDownDriverPool()
        when:
            sut.initDriverPool(driverSupplier)
        then:
            noExceptionThrown()
    }

    def "reinit current driver quits existing and returns new"() {
        given:
            def EFWebDriverMock2 = Mock(constructorArgs: [webDriverStub], EventFiringWebDriver)
            def driverMock2 = Mock(Driver) {
                getWebDriver() >> EFWebDriverMock2
            }
            def driverSupplier2 = { driverMock2 }

            def sut = new MultiUseDriverLifecycle(1)
            sut.initDriverPool(driverSupplier)
            sut.initBrowserBeforeTest(null)
            assert sut.getWebDriver() == EFWebDriverMock
        when:
            sut.reinitialiseCurrentDriver(driverSupplier2)
            sut.initBrowserBeforeTest(null)
        then:
            sut.getWebDriver() == EFWebDriverMock2
    }
}
