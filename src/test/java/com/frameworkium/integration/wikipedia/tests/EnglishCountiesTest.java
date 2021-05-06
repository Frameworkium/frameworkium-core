package com.frameworkium.integration.wikipedia.tests;

import com.frameworkium.lite.ui.tests.BaseUITest;
import com.frameworkium.integration.wikipedia.pages.EnglishCountiesPage;
import com.frameworkium.integration.wikipedia.pages.EnglishCountiesUsingListsPage;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test demonstrates the different between using
 * {@link com.frameworkium.lite.ui.element.StreamTable} and {@link java.util.List}s
 * of {@link org.openqa.selenium.WebElement}s.
 * <p>The trade-off is between readability, maintainability and performance.
 * <p>The List option is slightly longer and slightly more difficult to maintain,
 * especially if your table is changing shape (but not header text), however it
 * is significantly faster.
 */
@Test
public class EnglishCountiesTest extends BaseUITest {

    public void exploring_english_counties_data_with_stream_table() {
        var page = EnglishCountiesPage.open();

        assertThat(page.populationOf("Cornwall"))
                .isGreaterThan(550_000);
        // at least two counties have population densities of more than 3000
        assertThat(page.densities()
                .filter(density -> density > 3000)
                .limit(2)
                .count())
                .isEqualTo(2L);
    }


    public void exploring_english_counties_data_with_lists() {
        var page = EnglishCountiesUsingListsPage.open();

        assertThat(page.populationOf("Cornwall"))
                .isGreaterThan(550_000);
        // at least two counties have population densities of more than 3000
        assertThat(page.densities()
                .filter(density -> density > 3000)
                .limit(2)
                .count())
                .isEqualTo(2L);
    }
}
