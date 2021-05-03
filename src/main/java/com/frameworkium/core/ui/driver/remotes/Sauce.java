package com.frameworkium.core.ui.driver.remotes;

import com.frameworkium.core.common.properties.Property;
import com.google.common.collect.ImmutableMap;
import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.saucerest.SauceREST;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Sauce {

  private static final SauceOnDemandAuthentication sauceAuth =
      new SauceOnDemandAuthentication(
          System.getenv("SAUCE_USERNAME"),
          System.getenv("SAUCE_ACCESS_KEY"));

  private static final SauceREST client =
      new SauceREST(
          sauceAuth.getUsername(),
          sauceAuth.getAccessKey());

  public static URL getURL() {
    try {
      return new URL(String.format(
          "https://%s:%s@ondemand.saucelabs.com/wd/hub",
          sauceAuth.getUsername(),
          sauceAuth.getAccessKey()));
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static boolean isDesired() {
    return Property.SAUCE.getBoolean();
  }

  public static void updateJobName(SauceOnDemandSessionIdProvider sessionIdProvider, String name) {

    client.updateJobInfo(
        sessionIdProvider.getSessionId(),
        ImmutableMap.of("name", name));
  }

  public static void uploadFile(File file) throws IOException {
    client.uploadFile(file);
  }

}
