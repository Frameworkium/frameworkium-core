package com.frameworkium.core.ui.listeners;

import com.frameworkium.core.ui.driver.Driver;
import com.frameworkium.core.ui.driver.remotes.Sauce;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.testng.SauceOnDemandTestListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;

import static com.frameworkium.core.common.properties.Property.APP_PATH;

public class SauceLabsListener extends SauceOnDemandTestListener {

    private static final Logger logger = LogManager.getLogger();

    private boolean isRunningOnSauceLabs = Sauce.isDesired();

    @Override
    public void onStart(ITestContext testContext) {
        if (isRunningOnSauceLabs) {
            super.onStart(testContext);

            if (Driver.isNative()) {
                try {
                    Sauce.uploadFile(new File(APP_PATH.getValue()));
                } catch (IOException ioe) {
                    logger.error("Error uploading file", ioe);
                }
            }
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        if (isRunningOnSauceLabs) {
            // TODO: thread safe?
            Sauce.updateJobName(
                    (SauceOnDemandSessionIdProvider) result.getInstance(),
                    result.getTestClass().getRealClass().getSimpleName());

            super.onTestStart(result);
        }
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        if (isRunningOnSauceLabs) {
            super.onTestFailure(tr);
        }
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        if (isRunningOnSauceLabs) {
            super.onTestSuccess(tr);
        }
    }

}
