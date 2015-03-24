package com.github.tests.web;

import static com.google.common.truth.Truth.assertThat;

import org.testng.annotations.Test;

import ru.yandex.qatools.allure.annotations.Issue;

import com.frameworkium.tests.internal.BaseTest;
import com.github.pages.web.ExplorePage;
import com.github.pages.web.HomePage;
import com.github.pages.web.SearchResultsPage;

public class ComponentExampleTest extends BaseTest {

    @Issue("CET-1")
    @Test(description = "Simple test showing the use of components")
    public final void componentExampleTest() {

        // Navigate to homepage then use the nav bar to go to the explore page
        ExplorePage explorePage = HomePage.open().then().with().theHeader().clickExplore();

        // not a good assertion, improving this is an exercise for the reader
        assertThat(explorePage.getTitle()).isEqualTo("Explore · GitHub");

        // Search for "Selenium" and check SeleniumHQ/selenium is one of the returned reops.
        SearchResultsPage searchResultsPage = explorePage.with().theHeader().search("Selenium");
        assertThat(searchResultsPage.getRepoNames()).contains("SeleniumHQ/selenium");

        // Go back to the homepage
        HomePage homepage = searchResultsPage.with().theHeader().clickLogo();
        assertThat(homepage.getTitle()).isEqualTo("GitHub · Build software better, together.");
    }
}
