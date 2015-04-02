package com.frameworkium.capture.model;

import com.frameworkium.config.SystemProperty;

public class Browser {
    private String name;
    private String version;

    public Browser() {
        this.name = SystemProperty.BROWSER.getValue().toLowerCase();
        this.version = SystemProperty.BROWSER_VERSION.getValue();
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
