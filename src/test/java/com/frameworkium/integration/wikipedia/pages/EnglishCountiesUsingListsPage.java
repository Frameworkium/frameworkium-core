package com.frameworkium.integration.wikipedia.pages;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import com.frameworkium.core.ui.pages.PageFactory;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This page uses {@link List}s of {@link WebElement}s for the columns we know
 * that we need. This test is faster than using StreamTable, especially when
 * running over a grid due to the far fewer page lookups required.
 *
 * <p>This is a trade-off between readability, maintainability and performance.
 *
 * <p>This approach is great if the table and tests are unlikely to change often.
 */
public class EnglishCountiesUsingListsPage extends BasePage<EnglishCountiesUsingListsPage> {

    @Visible(checkAtMost = 1)
    @CacheLookup
    @FindBy(css = "table.wikitable > tbody > tr > td:nth-child(1)")
    private List<WebElement> countyColumn;

    @CacheLookup
    @FindBy(css = "table.wikitable > tbody > tr > td:nth-child(2)")
    private List<WebElement> populationColumn;

    @CacheLookup
    @FindBy(css = "table.wikitable > tbody > tr > td:nth-child(7)")
    private List<WebElement> densityColumn;

    public static EnglishCountiesUsingListsPage open() {
        return PageFactory.newInstance(EnglishCountiesUsingListsPage.class,
                "https://en.wikipedia.org/wiki/List_of_ceremonial_counties_of_England");
    }

    public int populationOf(String countyName) {
        String population =
                populationColumn.get(findCountyIndex(countyName))
                        .getText()
                        .replaceAll(",", "");
        return Integer.parseInt(population);
    }

    private int findCountyIndex(String countyName) {
        return IntStream.range(0, countyColumn.size())
                .filter(i -> Objects.equals(countyColumn.get(i).getText(), countyName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "County name '" + countyName + "' not found"));
    }

    public Stream<Integer> densities() {
        return densityColumn.stream()
                .map(WebElement::getText)
                .map(density -> density.replaceAll(",", ""))
                .map(Integer::parseInt);
    }

}
