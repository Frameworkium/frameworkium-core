package com.frameworkium.integration.frameworkium.tests;

import com.frameworkium.lite.ui.UITestLifecycle;
import com.frameworkium.lite.ui.tests.BaseUITest;
import com.frameworkium.integration.frameworkium.pages.JQueryDemoPage;
import com.frameworkium.integration.seleniumhq.pages.SeleniumDownloadPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

// tests if BeforeMethods are run despite not being in this group
@Test(groups = "fw-bugs")
public class FrameworkiumBugsTest extends BaseUITest {

    @BeforeMethod(dependsOnMethods = "configureBrowserBeforeTest")
    public void configureBrowserBeforeUse_allows_browser_access_in_before_method() {
        assertThat(UITestLifecycle.get().getWebDriver().getPageSource()).isNotEmpty();
    }

    public void ensure_jQueryAjaxDone_does_not_fail() {
        String headingText =
                JQueryDemoPage.open()
                        .waitForJQuery()
                        .getHeadingText();
        assertThat(headingText).isEqualTo("jQuery UI Demos");
    }

    public void use_base_page_visibility() {
        SeleniumDownloadPage.open().waitForContent();
    }

    @Test(dependsOnMethods = {"use_base_page_visibility"})
    public void ensure_BaseUITest_wait_is_updated_after_browser_reset() {
        // tests bug whereby BasePage.wait wasn't updated after browser reset
        SeleniumDownloadPage.open().waitForContent();
    }

    public void use_various_loggers() {
        logger.info("Using BaseUITest logger");
        SeleniumDownloadPage.open().log();
    }

}
