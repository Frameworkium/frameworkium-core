package com.bootstrapium.tests.web;

import static com.google.common.truth.Truth.assertThat;

import org.testng.annotations.Test;

import com.bootstrapium.pages.web.ForSaleResultsPage;
import com.bootstrapium.pages.web.HomePage;
import com.bootstrapium.tests.internal.BaseTest;

public class RecentSearchWebTest extends BaseTest {

    @Test(description = "Perform search then check recent searches updated")
    public final void testRecentSearches() {
        // Search for property for sale in 'SE16'
        ForSaleResultsPage resultsPage = HomePage.open().then()
                .searchPropertiesForSale("SE16").then().findProperties();

        // Click logo to go back to the home page
        HomePage homePage = resultsPage.header().clickLogoToGoHome();

        // Check 'SE16' is listed in recent searches
        assertThat(homePage.getRecentSearches()).contains("SE16");
    }
}
