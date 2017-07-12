package com.frameworkium.integration.ai.capture.api.dto.executions;

/** Browser message. */
public class Browser extends BaseDTO<Browser> {
    public String name;
    private String version;

    static Browser newInstance() {
        Browser browser = new Browser();
        browser.name = "Firefox";
        browser.version = "49.0";
        return browser;
    }


}
