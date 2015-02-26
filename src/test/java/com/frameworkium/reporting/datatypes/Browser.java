package com.frameworkium.reporting.datatypes;

public class Browser {
    public String name;
    public String version;
    public String ua;
    public String platformOS;

    public Browser(String name, String version, String ua, String platformOS) {
        this.name = name;
        this.version = version;
        this.ua = ua;
        this.platformOS = platformOS;
    }
}
