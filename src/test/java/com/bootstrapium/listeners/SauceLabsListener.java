package com.bootstrapium.listeners;

import org.testng.ITestContext;
import org.testng.ITestResult;

import com.bootstrapium.config.Sauce;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.testng.SauceOnDemandTestListener;

public class SauceLabsListener extends SauceOnDemandTestListener {

    private boolean isRunningOnSauceLabs = Sauce.isDesired();

    @Override
    public void onStart(ITestContext testContext) {
        if (isRunningOnSauceLabs) {
            super.onStart(testContext);
        }
    }

    /**
     * @param result
     */
    @Override
    public void onTestStart(ITestResult result) {
        if (isRunningOnSauceLabs) {
            Sauce.updateJobName(
                    (SauceOnDemandSessionIdProvider) result.getInstance(),
                    result.getTestClass().getRealClass().getSimpleName());

            super.onTestStart(result);
        }
    }

    /**
     * @param tr
     */
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
