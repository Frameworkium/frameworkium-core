package com.frameworkium.integration.github.tests;

import com.frameworkium.core.ui.tests.BaseUITest;
import com.frameworkium.integration.github.pages.HomePage;
import io.qameta.allure.Issue;
import org.testng.annotations.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class ComponentExampleTest extends BaseUITest {

    /**
     * @deprecated Flaky test, preserved until new, robust component test exists
     */
    @Deprecated
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
    @Test
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
