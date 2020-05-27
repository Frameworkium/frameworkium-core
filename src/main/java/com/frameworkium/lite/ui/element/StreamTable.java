package com.frameworkium.lite.ui.element;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Stream;

/**
 * {@link StreamTable} is an {@link AbstractStreamTable}.
 *
 * <p>Along with the assumptions made by {@link AbstractStreamTable} this class
 * assumes the header cells are all selectable by {@code thead > tr > th}.
 * the rows are all selectable by {@code tbody > tr} and cells inside the rows
 * are selectable by {@code td}.
 *
 * <p>It can also cope where entire columns or rows are hidden.
 */
public class StreamTable extends AbstractStreamTable {

    @FindBy(css = "thead > tr > th")
    private List<WebElement> headerCells;

    @FindBy(css = "tbody > tr")
    private List<WebElement> rows;

    @Override
    protected Stream<WebElement> headerCells() {
        return headerCells.stream().filter(WebElement::isDisplayed);
    }

    @Override
    protected Stream<WebElement> rows() {
        return rows.stream().filter(WebElement::isDisplayed);
    }

    @Override
    protected By cellLocator() {
        return By.cssSelector("td");
    }

}
