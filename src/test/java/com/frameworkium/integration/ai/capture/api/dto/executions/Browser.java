package com.frameworkium.integration.ai.capture.api.dto.executions;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/** Browser message. */
public class Browser extends BaseDTO {
    public String name;
    public String version;

    public static Browser newInstance() {
        Browser browser = new Browser();
        browser.name = "Firefox";
        browser.version = "49.0";
        return browser;
    }


}
