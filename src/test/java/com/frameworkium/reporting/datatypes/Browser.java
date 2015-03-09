package com.frameworkium.reporting.datatypes;

import org.openqa.selenium.Platform;

public class Browser {
    public String name;
    public String version;
    public String ua;
    public Platform platform;

    public Browser(String name, String version, String ua, Platform platform) {
        this.name = name;
        this.version = version;
        this.ua = ua;
        this.platform = platform;
    }
}
