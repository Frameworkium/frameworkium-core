package com.frameworkium.core.ui.js.framework;

import com.frameworkium.core.ui.js.AbstractFramework;
import com.paulhammant.ngwebdriver.NgWebDriver;
import org.openqa.selenium.JavascriptExecutor;

public class Angular implements AbstractFramework {

    /**
     * Check if Angular is used.
     * {@inheritDoc}
     * */
    @Override
    public boolean isPresent(JavascriptExecutor javascriptExecutor) {
        return (Boolean) javascriptExecutor.executeScript(
                "return typeof angular == 'object';");
    }

    /**
     * Wait for Angular to be ready.
     *
     * {@inheritDoc} */
    @Override
    public void waitToBeReady(JavascriptExecutor javascriptExecutor) {
        new NgWebDriver(javascriptExecutor).waitForAngularRequestsToFinish();
    }
}
