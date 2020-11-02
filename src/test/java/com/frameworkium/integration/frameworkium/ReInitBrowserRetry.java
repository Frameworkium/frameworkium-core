package com.frameworkium.integration.frameworkium;

import org.openqa.selenium.remote.UnreachableBrowserException;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class ReInitBrowserRetry implements IRetryAnalyzer {
    @Override
    public boolean retry(ITestResult result) {
        return result.getThrowable() instanceof UnreachableBrowserException;
    }
}
