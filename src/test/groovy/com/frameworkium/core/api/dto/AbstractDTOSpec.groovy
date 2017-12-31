package com.frameworkium.core.api.dto

import spock.lang.Specification

class AbstractDTOSpec extends Specification {

    def sut = new TopLevelDTO()

    def "two different DTOs with the same data are equal but not =="() {
        given:
            def sut2 = new TopLevelDTO()
        expect:
            sut.equals(sut2)
            !sut.is(sut2) // == in Java
            sut.hashCode() == sut2.hashCode()
    }

    def "two different DTOs with different data are not equal"() {
        given:
            def sut2 = new TopLevelDTO()
            sut2.lowLevelDTO.data = "foo"
        expect:
            sut != sut2
            !sut.is(sut2) // == in Java
            sut.hashCode() != sut2.hashCode()
    }

    def "Cloning a DTOs makes a deep not shallow clone"() {
        given:
            def clone = sut.clone()
        when:
            clone.lowLevelDTO.data = "something"
        then:
            sut.lowLevelDTO.data != "something"
    }

    def "toString() creates readable output"() {
        expect:
            sut.toString() == 'TopLevelDTO[lowLevelDTO=LowLevelDTO[data=initial]]'
    }

}
