package com.frameworkium.core.ui.element;

import com.frameworkium.core.htmlelements.element.HtmlElement;
import com.google.common.collect.Streams;
import org.openqa.selenium.*;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * {@link AbstractStreamTable} is an {@link HtmlElement} which provides a Java 8
 * interface, i.e. {@link Stream} and {@link Optional}, to HTML tables.
 *
 * <p>The header cells, rows and locator for cells inside the rows are
 * supplied by the sub-classes.
 *
 * <p>Each {@link Stream} is lazy, therefore, unlike other Table implementations,
 * the entire table does not have to be read each time, only the minimal amount
 * required.
 *
 * <p>However, nothing is cached, so be careful with multiple calls to
 * get new {@link Stream}s. You might want to cache results in your test or page.
 *
 * <p>Some assumptions have been made to prevent the complexity exploding while
 * still providing something that is useful.
 *
 * <p>{@link AbstractStreamTable} works best on tables which are regular
 * i.e. have the same number of columns in every row.
 *
 * <p>And DO NOT use:
 *
 * <ul>
 * <li>{@code rowspan}</li>
 * <li>{@code colspan}</li>
 * <li>{@code tfoot}</li>
 * </ul>
 *
 * <p>If any of these assumptions are invalid for your table then
 * {@link AbstractStreamTable} might still be useful, but use with caution.
 */
public abstract class AbstractStreamTable extends HtmlElement {

    /**
     * @return a {@link Stream} of {@link WebElement}s representing the header
     *         cells of the table.
     */
    protected abstract Stream<WebElement> headerCells();

    /**
     * @return a {@link Stream} of {@link WebElement}s representing the rows
     *         of the table.
     */
    protected abstract Stream<WebElement> rows();

    /**
     * @return a {@link By} to select cells inside the table rows.
     */
    protected abstract By cellLocator();

    /**
     * @return {@link Stream} of {@link WebElement}s representing the table
     *         heading cells.
     */
    public Stream<WebElement> getHeadings() {
        return headerCells();
    }

    /**
     * @param index 0-based index
     * @return {@link Optional} of the heading specified by the index
     */
    public Optional<WebElement> getHeading(long index) {
        return getHeadings().skip(index).findFirst();
    }

    /**
     * @param text text of the header cell to return
     * @return {@link Optional} of the first heading with text matching {@code text}
     */
    public Optional<WebElement> getHeading(String text) {
        return getHeading(e -> Objects.equals(e.getText().trim(), text));
    }

    /**
     * @param headerMatcher matcher for of the header cell to return
     * @return {@link Optional} of the first heading matching {@code headerMatcher}
     */
    public Optional<WebElement> getHeading(Predicate<WebElement> headerMatcher) {
        return getHeadings().filter(headerMatcher).findFirst();
    }

    /**
     * @return {@link Stream} of {@link Stream} of row cell {@link WebElement}s
     */
    public Stream<Stream<WebElement>> getRows() {
        return rows()
                .map(row -> row.findElements(cellLocator()).stream());
    }

    /**
     * @param index 0-based index
     * @return {@link Optional} of the row specified by the index
     */
    public Optional<Stream<WebElement>> getRow(int index) {
        return getRows().skip(index).findFirst();
    }

    /**
     * Useful when the index is already known, or in other cases where the other
     * methods do not work as expected, e.g. on tables which violate assumptions.
     *
     * @param index 0-based index of the column to return
     * @return {@link Stream} of cells in the table column indexed {@code index}
     */
    public Stream<WebElement> getColumn(long index) {
        return getRows()
                .map(rowCells -> rowCells
                        .skip(index)
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException(
                                "A row doesn't have column index " + index)));
    }

    /**
     * @param headerText the text of the header we are looking for
     * @return a {@link Stream} of {@link WebElement}s of the cells inside
     *         for the first column that matches the trimmed text of a header.
     */
    public Stream<WebElement> getColumn(String headerText) {
        long index = getHeaderIndex(e -> Objects.equals(e.getText(), headerText));
        return getColumn(index);
    }

    /**
     * @param headerMatcher predicate to find the header that we are looking for
     * @return a {@link Stream} of {@link WebElement}s of the cells for the
     *         first column that matches the {@code headerMatcher}
     */
    public Stream<WebElement> getColumn(Predicate<WebElement> headerMatcher) {
        return getColumn(getHeaderIndex(headerMatcher));
    }

    /**
     * Trims the text from the cells before comparing.
     *
     * @param lookupColHeaderText the String to match the header of the column
     *                            where we want to lookup using {@code lookupCellText}
     * @param lookupCellText      the String to match the cell(s) in the column
     *                            identified by {@code lookupColHeaderText} in the
     *                            column identified by {@code targetColHeaderText}
     * @param targetColHeaderText the String to match the header containing the
     *                            return value(s)
     * @return all {@link WebElement}s from the column matched by
     *         {@code targetColHeaderText} which matches {@code lookupCellText}
     *         in {@code lookupColHeaderText}
     * @see #getCellsByLookup(Predicate, Predicate, Predicate)
     */
    public Stream<WebElement> getCellsByLookup(
            String lookupColHeaderText, String lookupCellText, String targetColHeaderText) {
        return getCellsByLookup(
                element -> element.getText().trim().equals(lookupColHeaderText),
                element -> element.getText().trim().equals(lookupCellText),
                element -> element.getText().trim().equals(targetColHeaderText)
        );
    }

    /**
     * Returns cells in the target column where the corresponding cell matches
     * in the lookup column.
     *
     * <pre>
     * +--+--------+---------+
     * |  | lookup |  target |
     * +--+--------+---------+
     * |  | match---->return |
     * |  |    |   |         |
     * |  | match---->return |
     * +--+--------+---------+
     * </pre>
     *
     * @param lookupHeaderMatcher matches the header of the column where we want
     *                            to lookup using {@code lookupCellMatcher}
     * @param lookupCellMatcher   matches the cell(s) in the column
     *                            identified by {@code lookupHeaderMatcher} in the
     *                            column identified by {@code targetHeaderMatcher}
     * @param targetHeaderMatcher matches the header containing the return value(s)
     * @return {@link WebElement}s from the column matched by
     *         {@code lookupHeaderMatcher} which matches {@code lookupCellMatcher}
     *         in {@code lookupHeaderMatcher}
     */
    public Stream<WebElement> getCellsByLookup(
            Predicate<WebElement> lookupHeaderMatcher,
            Predicate<WebElement> lookupCellMatcher,
            Predicate<WebElement> targetHeaderMatcher) {

        long lookupColumnIndex = getHeaderIndex(lookupHeaderMatcher);
        long targetColumnIndex = getHeaderIndex(targetHeaderMatcher);
        Stream<WebElement> lookupColumn = getColumn(lookupColumnIndex);
        Stream<WebElement> targetColumn = getColumn(targetColumnIndex);

        return Streams.zip(
                lookupColumn,
                targetColumn,
                (lookupCell, targetCell) ->
                        lookupCellMatcher.test(lookupCell) ? targetCell : null)
                .filter(Objects::nonNull);
    }

    private long getHeaderIndex(Predicate<WebElement> headerPredicate) {
        return Streams.mapWithIndex(
                getHeadings(), (webElement, i) -> headerPredicate.test(webElement) ? i : null)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No header found."));
    }

}
