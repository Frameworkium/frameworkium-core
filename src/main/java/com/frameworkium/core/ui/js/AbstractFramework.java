package com.frameworkium.core.ui.js;

import org.openqa.selenium.JavascriptExecutor;

public interface AbstractFramework {

    /**
     * Checks if the Javascript Framework corresponding to the concrete java
     * class is present on the page.
     *
     * @param javascriptExecutor the javascriptExecutor to use.
     * @return true if the Javascript framework is present on the page.
     */
    boolean isPresent(JavascriptExecutor javascriptExecutor);

    /**
     * Waits for the Javascript Framework corresponding to the concrete java
     * class to finish loading.
     *
     * @param javascriptExecutor the javascriptExecutor to use.
     */
    void waitToBeReady(JavascriptExecutor javascriptExecutor);

}
