package com.frameworkium.core.ui.js.framework;

import com.frameworkium.core.ui.js.AbstractFramework;
import com.paulhammant.ngwebdriver.NgWebDriver;
import org.openqa.selenium.JavascriptExecutor;

public class Angular implements AbstractFramework {

    /** {@inheritDoc} */
    @Override
    public boolean isPresent(JavascriptExecutor javascriptExecutor) {
        return (Boolean) javascriptExecutor.executeScript(
                "return typeof angular == 'object';");
    }

    /** {@inheritDoc} */
    @Override
    public void waitToBeReady(JavascriptExecutor javascriptExecutor) {
        new NgWebDriver(javascriptExecutor).waitForAngularRequestsToFinish();
    }
}
