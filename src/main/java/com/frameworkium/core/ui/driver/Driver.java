package com.frameworkium.core.ui.driver;

import static com.frameworkium.core.common.properties.Property.APP_PATH;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

public interface Driver {

  /**
   * Check whether the driver is for a mobile device.
   */
  static boolean isMobile() {
    return false;
  }

  /**
   * Check whether the driver is for a native mobile app.
   */
  static boolean isNative() {
    return APP_PATH.isSpecified();
  }

  /**
   * Method to set-up the driver object.
   */
  void initialise();

  /**
   * Implemented in each Driver Type to specify the capabilities of that browser.
   *
   * @return Capabilities of each browser
   */
  Capabilities getCapabilities();

  /**
   * Returns the correct WebDriver object for the Driver Type.
   *
   * @param capabilities Capabilities of the browser
   * @return {@link WebDriver} object for the browser
   */
  WebDriver getWebDriver(Capabilities capabilities);

  /**
   * Getter for the driver that wraps the initialised driver.
   *
   * @return EventFiringWebDriver
   */
  EventFiringWebDriver getWebDriver();
}
