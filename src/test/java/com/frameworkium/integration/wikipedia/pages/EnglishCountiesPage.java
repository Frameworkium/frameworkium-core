package com.frameworkium.integration.wikipedia.pages;

import com.frameworkium.lite.ui.annotations.Visible;
import com.frameworkium.lite.ui.element.OptimisedStreamTable;
import com.frameworkium.lite.ui.pages.BasePage;
import com.frameworkium.lite.ui.pages.PageFactory;

import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * This page uses OptimisedStreamTable, this is slower than using Lists of
 * WebElements for columns, especially when running over a grid due to the far
 * greater number of page lookups required. This is even worse for StreamTable,
 * but StreamTable copes with a wider variety of Tables.
 *
 * <p>This is a trade-off between readability, maintainability and performance.
 *
 * <p>This approach is great if the table and tests are likely to change often.
 */
public class EnglishCountiesPage extends BasePage<EnglishCountiesPage> {

    @Visible
    @CacheLookup
    @FindBy(css = "table.wikitable") // luckily there's only one
    private OptimisedStreamTable listTable;

    public static EnglishCountiesPage open() {
        return PageFactory.newInstance(EnglishCountiesPage.class,
                "https://en.wikipedia.org/wiki/List_of_ceremonial_counties_of_England");
    }

    public int populationOf(String countyName) {
        Predicate<WebElement> lookUpCellMatcher = e -> e.getText().trim().equals(countyName);
        int lookupColumnIndex = 0;
        int targetColumnIndex = 1;
        String population = listTable
                .getCellsByLookupAndIndex(lookUpCellMatcher, lookupColumnIndex, targetColumnIndex)
                .findFirst()
                .orElseThrow(NotFoundException::new)
                .getText()
                .replaceAll(",", "");
        return Integer.parseInt(population);
    }

    public Stream<Integer> densities() {
        return listTable
                // hard-coded index because headers are now row-span=2
                .getColumn(6)
                .map(WebElement::getText)
                .map(density -> density.replaceAll(",", ""))
                .map(Integer::parseInt);
    }

}
