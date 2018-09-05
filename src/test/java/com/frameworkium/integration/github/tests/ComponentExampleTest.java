package com.frameworkium.integration.github.tests;

import com.frameworkium.core.common.retry.RetryFlakyTest;
import com.frameworkium.core.ui.tests.BaseUITest;
import com.frameworkium.integration.github.pages.HomePage;
import io.qameta.allure.Issue;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class ComponentExampleTest extends BaseUITest {

    @BeforeMethod(dependsOnMethods = "configureBrowserBeforeTest")
    public void maximizeBrowser() {
        // Maximises the browse just for these tests.
        // Assumes the resolution of the available GUI is sufficient to prevent
        // GitHub page from breaking
        BaseUITest.getDriver().getWrappedDriver().manage().window().maximize();
    }

    @TmsLink("CET-1")
    @Test(retryAnalyzer = RetryFlakyTest.class)
    public final void component_example_test() {

        // Search for "Selenium" and check SeleniumHQ/selenium is one of the returned repos.
        List<String> searchResults = HomePage.open()
                .with().theHeader()
                .clickExplore()
                .with().theHeader()
                .search("Selenium")
                .getRepoNames();

        assertThat(searchResults).contains("SeleniumHQ/selenium");
    }

    @Issue("Force Visible")
    @Test(description = "force visible makes hidden element visible")
    public void force_visible_makes_hidden_element_visible() {
        HomePage.open()
                .with().theHeader()
                .testForceVisible();
    }

    @Issue("Wait update")
    @Test(dependsOnMethods = {"force_visible_makes_hidden_element_visible"})
    public void ensure_BaseUITest_wait_is_updated_after_browser_reset() {
        // tests bug whereby BasePage.wait wasn't updated after browser reset
        HomePage.open().waitForSomething();
    }
}
