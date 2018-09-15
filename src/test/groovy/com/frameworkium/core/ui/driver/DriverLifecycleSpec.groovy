package com.frameworkium.core.ui.driver

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.events.EventFiringWebDriver
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class DriverLifecycleSpec extends Specification {

    def webDriverMock =
            Mock(constructorArgs: [Stub(WebDriver)], EventFiringWebDriver)
    def driverMock = Mock(Driver) {
        getWebDriver() >> webDriverMock
    }
    def driverSupplier = { driverMock }

    def "following expected lifecycle yields correct driver with DriverLifecycle(#poolSize, #reuseBrowser)"() {
        given:
            def sut = new DriverLifecycle(poolSize, reuseBrowser)

        when:
            sut.initDriverPool(driverSupplier)
            sut.initBrowserBeforeTest(driverSupplier)
            assert sut.getWebDriver() == webDriverMock
            sut.tearDownDriver()
            sut.tearDownDriverPool()
        then:
            webDriverMock.manage() >> Stub(WebDriver.Options)
            noExceptionThrown()
        where:
            poolSize || reuseBrowser
            1        || true
            2        || true
            1        || false
            2        || false
    }

    def "if a driver fails to tearDown exception will be thrown"() {
        given:
            def sut = new DriverLifecycle(1, false)
            sut.initBrowserBeforeTest(driverSupplier)
        when:
            sut.tearDownDriver()
        then:
            1 * webDriverMock.quit() >> { throw new Exception("") }
            thrown Exception
    }

    def "when reuseBrowser is true throwing exception on quit does not prevent subsequent drivers quitting"() {
        given:
            def sut = new DriverLifecycle(2, true)
            sut.initDriverPool(driverSupplier)
        when:
            sut.tearDownDriverPool()
        then:
            2 * webDriverMock.quit() >>> { throw new Exception("some error") } >> { }
    }

    def "when reuseBrowser is true initDriverPool can only be called once"() {
        given:
            def sut = new DriverLifecycle(1, true)
            sut.initDriverPool(driverSupplier)
        when:
            sut.initDriverPool(driverSupplier)
        then:
            thrown IllegalStateException
    }

    def "when reuseBrowser is true initDriverPool can only be called again after tearDownDriverPool"() {
        given:
            def sut = new DriverLifecycle(1, true)
            sut.initDriverPool(driverSupplier)
            sut.tearDownDriverPool()
        when:
            sut.initDriverPool(driverSupplier)
        then:
            noExceptionThrown()
    }
}
