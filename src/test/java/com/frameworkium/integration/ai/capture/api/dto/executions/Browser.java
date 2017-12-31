package com.frameworkium.integration.ai.capture.api.dto.executions;

import com.frameworkium.core.api.dto.AbstractDTO;

public class Browser extends AbstractDTO {

    public String name;
    public String version;

    public static Browser newInstance() {
        Browser browser = new Browser();
        browser.name = "Firefox";
        browser.version = "49.0";
        return browser;
    }
}
