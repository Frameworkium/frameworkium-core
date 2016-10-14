package com.frameworkium.core.api;

public interface Endpoint {

    /**
     * Calls {@link String#format(String, Object...)} on the url.
     *
     * @param params Arguments referenced by the format specifiers in the url.
     * @return A formatted String representing the URL of the given constant.
     */
    String getUrl(Object... params);
}
