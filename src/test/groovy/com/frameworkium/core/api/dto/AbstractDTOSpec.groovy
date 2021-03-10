package com.frameworkium.core.api.dto

import spock.lang.Specification

class AbstractDTOSpec extends Specification {

    def sut = new TopLevelDTO()

    def "two different DTOs with the same data are equal but not =="() {
        given:
            def other = new TopLevelDTO()
        expect:
            sut.equals(other)
            !sut.is(other) // == in Java
            sut.hashCode() == other.hashCode()
    }

    def "two different DTOs with different data are not equal"() {
        given:
            def other = new TopLevelDTO()
            other.lowLevelDTO.data = "foo"
        expect:
            sut != other
            !sut.is(other) // == in Java
            sut.hashCode() != other.hashCode()
    }

    def "DTOs of different types are not equal"() {
        given:
            def other = new LowLevelDTO()
        expect:
            sut != other
            !sut.is(other) // == in Java
            sut.hashCode() != other.hashCode()
    }

    def "a DTO is not equal to a non-DTO"() {
        given:
            def other = new Object()
        expect:
            sut != other
            !sut.is(other) // == in Java
            sut.hashCode() != other.hashCode()
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
            sut.toString() == 'TopLevelDTO[lowLevelDTO=LowLevelDTO[data=initial],stringList=[1,a]]'
    }

}
