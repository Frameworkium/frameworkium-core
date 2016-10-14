package com.frameworkium.integration.ai.capture.api.dto.executions;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/** Browser message. */
public class Browser {
    public String name;
    public String version;

    public static Browser newInstance() {
        Browser browser = new Browser();
        browser.name = "Firefox";
        browser.version = "49.0";
        return browser;
    }

    @Override
    public String toString() {
        return "Browser{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Browser browser = (Browser) o;

        return new EqualsBuilder()
                .append(name, browser.name)
                .append(version, browser.version)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(version)
                .toHashCode();
    }
}
