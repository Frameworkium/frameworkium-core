package com.frameworkium.core.common.retry;

import com.frameworkium.core.common.properties.Property;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryFlakyTest implements IRetryAnalyzer {

    /** Maximum retry count of failed tests, defaults to 1. */
    private static final int MAX_RETRY_COUNT =
            Property.MAX_RETRY_COUNT.isSpecified()
                    ? Integer.valueOf(Property.MAX_RETRY_COUNT.getValue())
                    : 1;

    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        return retryCount++ < MAX_RETRY_COUNT;
    }
}