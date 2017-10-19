package com.frameworkium.core.ui.js.framework;

import com.frameworkium.core.ui.js.AbstractFramework;
import com.paulhammant.ngwebdriver.NgWebDriver;
import org.openqa.selenium.JavascriptExecutor;

public class AngularTwo implements AbstractFramework {

    /**
     * Check if this is an Angular2 page.
     * {@inheritDoc}
     **/
    @Override
    public boolean isPresent(JavascriptExecutor javascriptExecutor) {
        return (Boolean) javascriptExecutor.executeScript(
                "return typeof ng == 'object';");
    }

    /**
     * Wait until Angular2 is completed.
     * {@inheritDoc}
     **/
    @Override
    public void waitToBeReady(JavascriptExecutor javascriptExecutor) {
        new NgWebDriver(javascriptExecutor).waitForAngular2RequestsToFinish();
    }
}
