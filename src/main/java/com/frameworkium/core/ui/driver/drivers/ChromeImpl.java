package com.frameworkium.core.ui.driver.drivers;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.driver.AbstractDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeImpl extends AbstractDriver {

  @Override
  public ChromeOptions getCapabilities() {
    var chromeOptions = new ChromeOptions();
    chromeOptions.setHeadless(Property.HEADLESS.getBoolean());
    return chromeOptions;
  }

  @Override
  public WebDriver getWebDriver(Capabilities capabilities) {
    final ChromeOptions chromeOptions;
    if (capabilities instanceof ChromeOptions) {
      chromeOptions = (ChromeOptions) capabilities;
    } else {
      chromeOptions = new ChromeOptions().merge(capabilities);
    }
    return new ChromeDriver(chromeOptions);
  }

}
