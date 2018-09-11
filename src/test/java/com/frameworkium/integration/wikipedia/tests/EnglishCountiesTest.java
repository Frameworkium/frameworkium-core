package com.frameworkium.integration.wikipedia.tests;

import com.frameworkium.core.ui.tests.BaseUITest;
import com.frameworkium.integration.wikipedia.pages.EnglishCountiesPage;
import com.frameworkium.integration.wikipedia.pages.EnglishCountiesUsingListsPage;
import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * This test demonstrates the different between using the StreamTable and Lists
 * of WebElements.
 * <p>The trade-off is between readability, maintainability and performance.
 * <p>The List option is slightly longer
 */
public class EnglishCountiesTest extends BaseUITest {

    @TmsLink("WIKI-2")
    @Test(description = "Playing with English Counties data")
    public final void exploring_english_counties_data_with_stream_table() {
        EnglishCountiesPage page = EnglishCountiesPage.open();

        assertThat(page.populationOf("Cornwall"))
                .isAtLeast(550_000);
        // at least two counties have population densities of more than 3000
        assertThat(page.densities()
                .filter(density -> density > 3000)
                .limit(2)
                .count())
                .isEqualTo(2L);
    }


    @TmsLink("WIKI-2")
    @Test(description = "Playing with English Counties data")
    public final void exploring_english_counties_data_with_lists() {
        EnglishCountiesUsingListsPage page = EnglishCountiesUsingListsPage.open();

        assertThat(page.populationOf("Cornwall"))
                .isAtLeast(550_000);
        // at least two counties have population densities of more than 3000
        assertThat(page.densities()
                .filter(density -> density > 3000)
                .limit(2)
                .count())
                .isEqualTo(2L);
    }
}
