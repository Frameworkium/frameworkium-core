package com.frameworkium.listeners;

import static com.frameworkium.config.SystemProperty.APP_PATH;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestResult;

import com.frameworkium.config.DriverType;
import com.frameworkium.config.Sauce;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.testng.SauceOnDemandTestListener;

public class SauceLabsListener extends SauceOnDemandTestListener {

    private Logger logger = LogManager.getLogger(SauceLabsListener.class);
    private boolean isRunningOnSauceLabs = Sauce.isDesired();

    @Override
    public void onStart(ITestContext testContext) {
        if (isRunningOnSauceLabs) {
            super.onStart(testContext);

            if (DriverType.isNative()) {
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
            Sauce.updateJobName((SauceOnDemandSessionIdProvider) result.getInstance(), result.getTestClass()
                    .getRealClass().getSimpleName());

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
