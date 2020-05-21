package com.frameworkium.integration.angularjs.tests;

import com.frameworkium.core.ui.tests.BaseUITest;
import com.frameworkium.integration.angularjs.pages.DeveloperGuidePage;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;

public class DocumentationTest extends BaseUITest {

    @Test
    public void angular_documentation_test() {
        String guideTitle = DeveloperGuidePage.open()
                .searchDeveloperGuide("Bootstrap")
                .clickBootstrapSearchItem()
                .getGuideTitle();

        assertThat(guideTitle)
                .endsWith("Bootstrap");
    }

}
