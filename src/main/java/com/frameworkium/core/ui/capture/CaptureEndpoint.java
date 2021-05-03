package com.frameworkium.core.ui.capture;

import com.frameworkium.core.api.Endpoint;
import com.frameworkium.core.common.properties.Property;

/**
 * The various Endpoints of Capture.
 */
enum CaptureEndpoint implements Endpoint {

  BASE_URI(Property.CAPTURE_URL.getValue()),
  EXECUTIONS(BASE_URI.url + "/executions"),
  SCREENSHOT(BASE_URI.url + "/screenshot");

  private String url;

  CaptureEndpoint(String url) {
    this.url = url;
  }

  @Override
  public String getUrl(Object... params) {
    return String.format(url, params);
  }

}
