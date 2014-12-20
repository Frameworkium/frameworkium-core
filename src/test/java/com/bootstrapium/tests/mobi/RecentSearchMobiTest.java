package com.bootstrapium.tests.mobi;

import static com.google.common.truth.Truth.assertThat;

import org.testng.annotations.Test;

import com.bootstrapium.pages.mobi.ForSaleResultsPage;
import com.bootstrapium.pages.mobi.HomePage;
import com.bootstrapium.tests.internal.BaseTest;

public class RecentSearchMobiTest extends BaseTest {

    @Test(description = "Perform search then check recent searches updated")
    public final void testRecentSearches() {
        // Search for property for sale in 'SE16'
        ForSaleResultsPage resultsPage = HomePage.open().then()
                .clickForSale().then().enterLocation("SE16").then().findProperties();

        // Click logo to go back to the home page
        HomePage homePage = resultsPage.header().clickLogoToGoHome();

        // Check 'SE16' is listed in recent searches
        assertThat(homePage.getRecentSearches()).contains("SE16, for sale");
    }
}
