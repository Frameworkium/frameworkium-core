package com.frameworkium.core.api;

public interface Endpoint {

    /**
     * Calls {@link String#format(String, Object...)} on the url.
     *
     * @param params Arguments referenced by the format specifiers in the url.
     * @return A formatted URL String
     */
    String getUrl(Object... params);
}
