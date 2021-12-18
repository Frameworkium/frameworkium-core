package com.frameworkium.core.ui.video

import org.apache.commons.io.FileUtils
import spock.lang.Specification

import java.nio.file.Files
import java.util.concurrent.TimeoutException

class VideoCaptureSpec extends Specification {

    def setup() {
        FileUtils.deleteDirectory(VideoCapture.VIDEO_FOLDER.toFile())
    }

    def array = [0, 1, 0, 1] as byte[]
    def urlFetcherMock = Mock(UrlFetcher) {
        fetchWithRetry(_, _) >> array
    }
    def url = "http://foo/bar/%s.ext"


    def "fetches video for stored testname"() {
        given:
            def sut = new VideoCapture(url, urlFetcherMock)
            sut.saveTestSessionID("test1", "session1")
        when:
            sut.fetchAndSaveVideo("test1")
        then:
            def filepath = VideoCapture.VIDEO_FOLDER.resolve("test1-session1.ext")
            Files.readAllBytes(filepath) == array
    }

    def "doesn't save file on fetch timeout"() {
        given:
            def timingOutUrlFetcherMock = Mock(UrlFetcher) {
                fetchWithRetry(_, _) >> { throw new TimeoutException("test2") }
            }
            def sut = new VideoCapture(url, timingOutUrlFetcherMock)
            sut.saveTestSessionID("test2", "session2")
        when:
            sut.fetchAndSaveVideo("test2")
        then:
            Files.notExists(VideoCapture.VIDEO_FOLDER.resolve("test2-session2.ext"))
    }

    def "invalid url throws IllegalArgumentException"() {
        given:
            def sut = new VideoCapture("abc%^!", urlFetcherMock)
            sut.saveTestSessionID("test3", "session3")
        when:
            sut.fetchAndSaveVideo("test3")
        then:
            thrown IllegalArgumentException
    }
}
