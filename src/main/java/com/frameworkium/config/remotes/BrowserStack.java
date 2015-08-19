package com.frameworkium.config.remotes;

import static com.frameworkium.config.SystemProperty.BROWSER_STACK;

import java.net.MalformedURLException;
import java.net.URL;

public class BrowserStack {

    public static URL getURL() throws MalformedURLException {
        return new URL(String.format("http://%s:%s@hub.browserstack.com:80/wd/hub", System.getenv("BROWSER_STACK_USERNAME"),
                System.getenv("BROWSER_STACK_ACCESS_KEY")));
    }

    public static boolean isDesired() {
        return BROWSER_STACK.isSpecified() && Boolean.parseBoolean(BROWSER_STACK.getValue());
    }

    private static boolean environmentVariablesSet() {
        String username = System.getenv("BROWSER_STACK_USERNAME");
        String accessKey = System.getenv("BROWSER_STACK_ACCESS_KEY");
        return username != null && !username.isEmpty() && accessKey != null && !accessKey.isEmpty();
    }
}
