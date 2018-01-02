package com.frameworkium.integration.github.tests.web;

import com.frameworkium.core.ui.tests.BaseUITest;
import com.frameworkium.integration.github.pages.web.ExplorePage;
import com.frameworkium.integration.github.pages.web.HomePage;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.TestCaseId;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class ComponentExampleTest extends BaseUITest {

    @Issue("CET-1")
    @Test(description = "Simple test showing the use of components")
    public final void component_example_test() {

        // Navigate to homepage then use the nav bar to go to the explore page
        ExplorePage explorePage = HomePage.open().then().with().theHeader().clickExplore();

        // not a great assertion, improving this is an exercise for the reader
        assertThat(explorePage.getTitle()).isEqualTo("Explore · GitHub");

        // Search for "Selenium" and check SeleniumHQ/selenium is one of the returned repos.
        List<String> searchResults = explorePage.with().theHeader()
                .search("Selenium")
                .getRepoNames();

        assertThat(searchResults).contains("SeleniumHQ/selenium");
    }

    @TestCaseId("Force Visible")
    @Test(description = "force visible makes hidden element visible")
    public void force_visible_makes_hidden_element_visible() {
        HomePage.open()
                .with().theHeader()
                .testForceVisible();
    }

    @TestCaseId("Wait update")
    @Test(dependsOnMethods = {"force_visible_makes_hidden_element_visible"})
    public void ensure_BaseUITest_wait_is_updated_after_browser_reset() {
        // tests bug whereby BasePage.wait wasn't updated after browser reset
        HomePage.open().waitForSomething();
    }
}
