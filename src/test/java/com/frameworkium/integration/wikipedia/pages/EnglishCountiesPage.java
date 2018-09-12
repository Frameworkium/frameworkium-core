package com.frameworkium.integration.wikipedia.pages;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.element.OptimisedStreamTable;
import com.frameworkium.core.ui.pages.BasePage;
import com.frameworkium.core.ui.pages.PageFactory;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

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
        String lookupHeader = "County";
        String targetHeader = "Population";
        String population = listTable
                .getCellsByLookup(lookupHeader, countyName, targetHeader)
                .findFirst()
                .orElseThrow(NotFoundException::new)
                .getText()
                .replaceAll(",", "");
        return Integer.parseInt(population);
    }

    public Stream<Integer> densities() {
        return listTable
                .getColumn(e -> e.getText().startsWith("Density"))
                .map(WebElement::getText)
                .map(density -> density.replaceAll(",", ""))
                .map(Integer::parseInt);
    }

}
