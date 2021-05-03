package com.frameworkium.core.api;

/**
 * Intended for an enum to store the various endpoints of your API under test.
 *
 * <p>The following is an example implementation:
 * <pre>{@code
 * public enum MyEndpoint implements Endpoint {
 *
 *     BASE_URI("https://xxx),
 *     YYY_ID("/yyy/%d");
 *
 *     private String url;
 *
 *     MyEndpoint(String url) {
 *         this.url = url;
 *     }
 *
 *     public String getUrl(Object... params) {
 *         return String.format(url, params);
 *     }
 *  }}</pre>
 *
 * <p>The key feature is an enum entry for each endpoint where the url String can
 * contain a {@code String.format} to enable easy injection of parameters.
 */
public interface Endpoint {

  /**
   * Calls {@link String#format(String, Object...)} on the url.
   *
   * @param params Arguments referenced by the format specifiers in the url.
   * @return A formatted String representing the URL of the given constant.
   */
  String getUrl(Object... params);
}
