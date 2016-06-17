package com.frameworkium.core.ui.js;

import org.openqa.selenium.JavascriptExecutor;

public interface AbstractFramework {

    /** {@inheritDoc} **/
    boolean isPresent(JavascriptExecutor javascriptExecutor);

    /** {@inheritDoc} **/
    void waitToBeReady(JavascriptExecutor javascriptExecutor);

}
