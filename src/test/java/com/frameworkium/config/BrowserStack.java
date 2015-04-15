package com.frameworkium.config;

import static com.frameworkium.config.SystemProperty.BROWSER_STACK;

import java.net.MalformedURLException;
import java.net.URL;

public class BrowserStack {

    static {
        if (isDesired() && !environmentVariablesSet()) {
            throw new RuntimeException("BROWSER_STACK_USERNAME and BROWSER_STACK_ACCESS_KEY environment variables must be set.");
        }
    }

    public static URL getURL() throws MalformedURLException {
        return new URL(String.format("http://%s:%s@hub.browserstack.com:80/wd/hub", System.getenv("BROWSER_STACK_USERNAME"),
                System.getenv("BROWSER_STACK_ACCESS_KEY")));
    }

    public static boolean isDesired() {
        return BROWSER_STACK.isSpecified() && Boolean.parseBoolean(BROWSER_STACK.getValue());
    }

    private static boolean environmentVariablesSet() {
        String username = System.getenv("BROWSER_STACK_USER");
        String accessKey = System.getenv("BROWSER_STACK_ACCESS_KEY");
        return username != null && !username.isEmpty() && accessKey != null && !accessKey.isEmpty();
    }
}
