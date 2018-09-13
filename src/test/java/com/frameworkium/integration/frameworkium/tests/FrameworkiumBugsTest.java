package com.frameworkium.integration.frameworkium.tests;

import com.frameworkium.core.ui.tests.BaseUITest;
import com.frameworkium.integration.frameworkium.pages.JQueryDemoPage;
import com.frameworkium.integration.seleniumhq.pages.SeleniumDownloadPage;
import com.frameworkium.integration.theinternet.pages.WelcomePage;
import org.testng.annotations.*;

import static com.google.common.truth.Truth.assertThat;

public class FrameworkiumBugsTest extends BaseUITest {

    @BeforeClass
    public void configureBrowserBeforeUse_allows_browser_access_in_before_class() {
        configureBrowserBeforeUse();
        WelcomePage.open();
    }

    public void ensure_jQueryAjaxDone_does_not_fail() {
        String headingText =
                JQueryDemoPage.open()
                        .waitForJQuery()
                        .getHeadingText();
        assertThat(headingText).isEqualTo("jQuery UI Demos");
    }

    public void use_base_page_visibility() {
        SeleniumDownloadPage.open()
                .hideContent()
                .forceVisibleContent()
                .waitForContent();
    }

    @Test(dependsOnMethods = {"use_base_page_visibility"})
    public void ensure_BaseUITest_wait_is_updated_after_browser_reset() {
        // tests bug whereby BasePage.wait wasn't updated after browser reset
        SeleniumDownloadPage.open().waitForContent();
    }

    public void use_base_page_logger() {
        SeleniumDownloadPage.open().log();
    }

}
