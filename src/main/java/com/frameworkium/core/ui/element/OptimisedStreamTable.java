package com.frameworkium.core.ui.element;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Stream;

/**
 * {@link OptimisedStreamTable} is an {@link AbstractStreamTable}.
 *
 * <p>Along with the assumptions made by {@link AbstractStreamTable} this class
 * assumes the header cells are all selectable by {@code thead > tr > th}
 * and the rows are all selectable by {@code tbody > tr}.
 *
 * <p>{@link OptimisedStreamTable} is approximately twice as fast as
 * {@link StreamTable} but it cannot cope with entire columns or rows
 * which are not displayed.
 */
public class OptimisedStreamTable extends AbstractStreamTable {

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

}
