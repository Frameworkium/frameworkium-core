package com.frameworkium.integration.ai.capture.api.dto.executions;

import com.frameworkium.core.api.dto.AbstractDTO;

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
