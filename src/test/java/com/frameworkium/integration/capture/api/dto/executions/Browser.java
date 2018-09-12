package com.frameworkium.integration.capture.api.dto.executions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frameworkium.core.api.dto.AbstractDTO;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Browser extends AbstractDTO<Browser> {

    public String name;
    public String version;

    public static Browser newInstance() {
        Browser browser = new Browser();
        browser.name = "Firefox";
        browser.version = "58.0";
        return browser;
    }
}
