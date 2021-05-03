package com.frameworkium.core.ui.driver.remotes;

import static com.frameworkium.core.common.properties.Property.BROWSER_STACK;

import java.net.MalformedURLException;
import java.net.URL;

public class BrowserStack {

  private BrowserStack() {
    // hide default constructor for this util class
  }

  public static URL getURL() throws MalformedURLException {
    return new URL(String.format("https://%s:%s@hub-cloud.browserstack.com/wd/hub",
        System.getenv("BROWSER_STACK_USERNAME"),
        System.getenv("BROWSER_STACK_ACCESS_KEY")));
  }

  public static boolean isDesired() {
    return BROWSER_STACK.getBoolean();
  }
}
