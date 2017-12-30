package com.frameworkium.integration.angularjs.tests.web;

import com.frameworkium.core.common.retry.RetryFlakyTest;
import com.frameworkium.core.ui.tests.BaseTest;
import com.frameworkium.integration.angularjs.pages.web.DeveloperGuidePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.TestCaseId;

import static com.google.common.truth.Truth.assertThat;

public class DocumentationTest extends BaseTest {

    @BeforeMethod
    public void test_configure_browser_before_use() {
        // TODO: figure out why this is here and if still needed with reuseBrowser
        configureBrowserBeforeUse();
        DeveloperGuidePage.open();
    }

    @Test(description =
            "Tests the AngularJS developer documentation and search function",
            retryAnalyzer = RetryFlakyTest.class)
    @TestCaseId("TEST-1")
    public void angular_documentation_test() {
        String guideTitle = DeveloperGuidePage.open()
                .searchDeveloperGuide("Bootstrap")
                .clickBootstrapSearchItem()
                .getGuideTitle();

        assertThat(guideTitle)
                .contains("Bootstrap");
    }

}
