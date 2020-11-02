package com.frameworkium.integration.frameworkium.tests;

import com.frameworkium.integration.frameworkium.ReInitBrowserRetry;
import com.frameworkium.integration.theinternet.pages.WelcomePage;
import com.frameworkium.lite.ui.tests.BaseUITest;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.testng.annotations.Test;

public class ReInitDriverTest extends BaseUITest {

    private boolean retry = true;

    @Test(retryAnalyzer = ReInitBrowserRetry.class)
    public void browser_is_re_initialised_if_test_is_restarted() {
        WelcomePage.open();
        if (retry) {
            retry = false;
            throw new UnreachableBrowserException("");
        }
    }
}
