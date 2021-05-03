package com.frameworkium.core.ui.capture.model;

import static com.frameworkium.core.common.properties.Property.BROWSER;
import static com.frameworkium.core.common.properties.Property.BROWSER_VERSION;
import static com.frameworkium.core.common.properties.Property.DEVICE;
import static com.frameworkium.core.common.properties.Property.PLATFORM;
import static com.frameworkium.core.common.properties.Property.PLATFORM_VERSION;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.frameworkium.core.ui.UITestLifecycle;
import com.frameworkium.core.ui.driver.DriverSetup;
import java.util.Optional;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Browser {

  public String name;
  public String version;
  public String device;
  public String platform;
  public String platformVersion;

  /**
   * Create browser object.
   */
  public Browser() {

    Optional<String> userAgent = UITestLifecycle.get().getUserAgent();
    if (userAgent.isPresent() && !userAgent.get().isEmpty()) {
      UserAgentStringParser uaParser = UADetectorServiceFactory.getResourceModuleParser();
      ReadableUserAgent agent = uaParser.parse(userAgent.get());

      this.name = agent.getName();
      this.version = agent.getVersionNumber().toVersionString();
      this.device = agent.getDeviceCategory().getName();
      this.platform = agent.getOperatingSystem().getName();
      this.platformVersion = agent.getOperatingSystem().getVersionNumber().toVersionString();

    } else {
      // Fall-back to the Property class
      if (BROWSER.isSpecified()) {
        this.name = BROWSER.getValue().toLowerCase();
      } else {
        this.name = DriverSetup.DEFAULT_BROWSER.toString();
      }
      if (BROWSER_VERSION.isSpecified()) {
        this.version = BROWSER_VERSION.getValue();
      }
      if (DEVICE.isSpecified()) {
        this.device = DEVICE.getValue();
      }
      if (PLATFORM.isSpecified()) {
        this.platform = PLATFORM.getValue();
      }
      if (PLATFORM_VERSION.isSpecified()) {
        this.platformVersion = PLATFORM_VERSION.getValue();
      }
    }
  }

}
