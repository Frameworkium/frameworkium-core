package com.lazerycode.selenium.tests.web;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.lazerycode.selenium.pages.web.ForSaleResultsPage;
import com.lazerycode.selenium.pages.web.HomePage;
import com.lazerycode.selenium.tests.internal.BaseTest;

public class RecentSearchWebTest extends BaseTest {

    @Test(description = "Perform search then check recent searches updated")
    public final void testRecentSearches() {
        // Search for property for sale in 'SE16'
        ForSaleResultsPage resultsPage = HomePage.open().then()
                .searchPropertiesForSale("SE16").then().findProperties();

        // Click logo to go back to the home page
        HomePage homePage = resultsPage.header().clickLogoToGoHome();

        // Check 'SE16' is listed in recent searches
        assertTrue(homePage.getRecentSearches().contains("SE16"),
                "Expected SE16 in recent searches");
    }
    
    @Test(description = "Perform search then check recent searches updated")
    public final void testRecentSearches2() {
        // Search for property for sale in 'SE16'
        ForSaleResultsPage resultsPage = HomePage.open().then()
                .searchPropertiesForSale("SE16").then().findProperties();

        // Click logo to go back to the home page
        HomePage homePage = resultsPage.header().clickLogoToGoHome();

        // Check 'SE16' is listed in recent searches
        assertTrue(homePage.getRecentSearches().contains("SE16"),
                "Expected SE16 in recent searches");
    }
}
