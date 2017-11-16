package com.frameworkium.core.ui.listeners

import org.openqa.selenium.TakesScreenshot
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

class ScreenshotListenerSpec extends Specification {

    def testName = "testName"
    def defaultScreenshotFolder = Paths.get("screenshots")

    def setup() {
        Files.createDirectories(defaultScreenshotFolder)
    }

    def cleanup() {
        listAllTestScreenshots()
                .map({ it.toFile() })
                .forEach({ it.delete() })
    }

    def sut = new ScreenshotListener()

    def "takeScreenshotAndSaveLocally takes a screenshot and saves file"() {
        given:
            TakesScreenshot mockDriver = Mock()
            def screenshotCount = listAllTestScreenshots().count()
        when:
            sut.takeScreenshotAndSaveLocally(testName, mockDriver)
        then:
            1 * mockDriver.getScreenshotAs(_) >> { [1, 2, 3] as byte[] }
            listAllTestScreenshots().count() == screenshotCount + 1

    }

    def listAllTestScreenshots() {
        Files.walk(defaultScreenshotFolder)
                .filter({ it.toString().endsWith(testName + ".png") })
    }

}
