package com.frameworkium.integration.angularjs.tests.web;

import com.frameworkium.core.ui.tests.BaseTest;
import com.frameworkium.integration.angularjs.pages.web.DeveloperGuidePage;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.TestCaseId;

import static com.google.common.truth.Truth.assertThat;

public class DocumentationTest extends BaseTest {

    @Test(description =
            "Tests the AngularJS developer documentation and search function")
    @TestCaseId("TEST-1")
    public void documentationTest() {
        String guideTitle = DeveloperGuidePage.open()
                .searchDeveloperGuide("Bootstrap")
                .clickBootstrapSearchItem()
                .getGuideTitle();

        assertThat(guideTitle)
                .isEqualTo("Bootstrap");
    }

}
