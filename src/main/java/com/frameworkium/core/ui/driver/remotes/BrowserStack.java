package com.frameworkium.core.ui.driver.remotes;

import java.net.MalformedURLException;
import java.net.URL;

import static com.frameworkium.core.common.properties.Property.BROWSER_STACK;

public class BrowserStack {

    private BrowserStack() {
        // hide default constructor for this util class
    }

    /**
     * Get the URL for the BrowserStack instance as configured by the parameters.
     */
    public static URL getURL() throws MalformedURLException {
        return new URL(String.format("http://%s:%s@hub.browserstack.com:80/wd/hub",
                System.getenv("BROWSER_STACK_USERNAME"),
                System.getenv("BROWSER_STACK_ACCESS_KEY")));
    }

    public static boolean isDesired() {
        return BROWSER_STACK.isSpecified()
                && Boolean.parseBoolean(BROWSER_STACK.getValue());
    }
}
