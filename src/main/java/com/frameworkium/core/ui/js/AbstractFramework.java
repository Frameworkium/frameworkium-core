package com.frameworkium.core.ui.js;

import org.openqa.selenium.JavascriptExecutor;

public interface AbstractFramework {

    /**
     * Checks if the Javascript Framework corresponding to the concrete Java
     * class is present on the page.
     */
    boolean isPresent(JavascriptExecutor javascriptExecutor);

    /**
     * Waits for the Javascript Framework corresponding to the concrete Java
     * class to finish loading.
     */
    void waitToBeReady(JavascriptExecutor javascriptExecutor);

}
