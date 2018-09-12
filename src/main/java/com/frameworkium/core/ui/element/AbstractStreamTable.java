package com.frameworkium.core.ui.element;

import com.google.common.collect.Streams;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * {@link AbstractStreamTable} is an {@link HtmlElement} which provides a Java 8
 * interface, i.e. {@link Stream} and {@link Optional}, to HTML tables.
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
 * <p>{@link AbstractStreamTable} works best on tables which:
 *
 * <ul>
 * <li>are regular i.e. same number of columns ({@code td}) in every row</li>
 * </ul>
 *
 * <p>and DO NOT use:
 *
 * <ul>
 * <li>{@code rowspan}</li>
 * <li>{@code colspan}</li>
 * <li>{@code tfoot}</li>
 * </ul>
 *
 * <p>The header cells and rows ({@link WebElement} containing the {@code td} tags)
 * are supplied by the sub-classes.
 *
 * <p>If any of these assumptions are invalid for your table then {@link AbstractStreamTable}
 * might still be useful but use the API with caution.
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
     * @return {@link Stream} of visible (i.e. displayed) {@code th} WebElements.
     */
    public Stream<WebElement> getHeadings() {
        return headerCells();
    }

    /**
     * @param index 0-based index
     * @return Optional of the heading specified by the index
     */
    public Optional<WebElement> getHeading(int index) {
        return getHeadings().skip(index).findFirst();
    }

    /**
     * @param text text of the header cell to return
     * @return Optional of the first heading matching {@code text}
     */
    public Optional<WebElement> getHeading(String text) {
        return getHeadings()
                .filter(e -> Objects.equals(e.getText().trim(), text))
                .findFirst();
    }

    /**
     * @param headerMatcher matcher for of the header cell to return
     * @return Optional of the first heading matching {@code headerMatcher}
     */
    public Optional<WebElement> getHeading(Predicate<WebElement> headerMatcher) {
        return getHeading(getHeaderIndex(headerMatcher));
    }

    /**
     * @return {@link Stream} of {@link Stream} of cell {@link WebElement}s
     */
    public Stream<Stream<WebElement>> getRows() {
        return rows()
                .map(el -> el.findElements(cellLocator()).stream());
    }

    /**
     * @param index 0-based index
     * @return the row specified by the index
     */
    public Optional<Stream<WebElement>> getRow(int index) {
        return getRows().skip(index).findFirst();
    }

    /**
     * Can be used where the index is already known or in other cases where the
     * other methods do not work as expected on tables which violate assumptions.
     *
     * @param index 0-based index of the column to return
     * @return {@link Stream} of cells in the table column indexed {@code index}
     */
    public Stream<WebElement> getColumn(int index) {
        return getRows()
                .map(cells -> cells.skip(index).findFirst()
                        .orElseThrow(() -> new NoSuchElementException(
                                "A row doesn't have column index " + index)));
    }

    /**
     * @param headerText the text of the header we are looking for
     * @return a {@link Stream} of {@link WebElement}s of the cells inside
     *         for the first column that matches the trimmed text of a header.
     */
    public Stream<WebElement> getColumn(String headerText) {
        int index = getHeaderIndex(e -> Objects.equals(e.getText(), headerText));
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
     * N.B. trims the text from the cells before comparing.
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
     * Returns {@link WebElement}s from target cells given a match in the lookup column
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

        int lookupColumnIndex = getHeaderIndex(lookupHeaderMatcher);
        int targetColumnIndex = getHeaderIndex(targetHeaderMatcher);
        Stream<WebElement> lookupColumn = getColumn(lookupColumnIndex);
        Stream<WebElement> targetColumn = getColumn(targetColumnIndex);

        return Streams.zip(
                lookupColumn,
                targetColumn,
                (lookupCell, targetCell) ->
                        lookupCellMatcher.test(lookupCell) ? targetCell : null)
                .filter(Objects::nonNull);
    }

    /**
     * Returns index of a header matching the {@code headerPredicate}.
     * This shouldn't be needed outside this class.
     * If you think it should be, raise a bug to discuss.
     */
    private int getHeaderIndex(Predicate<WebElement> headerPredicate) {
        List<WebElement> headings = getHeadings().collect(toList());
        return IntStream.range(0, headings.size())
                .filter(i -> headerPredicate.test(headings.get(i)))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "Table header " + headerPredicate + " not found"));
    }

}
