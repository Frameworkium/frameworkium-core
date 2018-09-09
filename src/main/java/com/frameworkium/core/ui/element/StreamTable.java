package com.frameworkium.core.ui.element;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * {@link StreamTable} is an {@link HtmlElement} which provides a Java 8 interface
 * i.e. Streams and Optional to tables.
 *
 * <p>Streams are lazy, this means that, unlike other Table implementations, the
 * entire table does not have to be read each time, only the minimal amount
 * required.
 *
 * <p>However, nothing is cached, so be careful with multiple calls to
 * get new {@link Stream}s. You might want to cache results in your test.
 *
 * <p>Some assumptions have been made to prevent the complexity exploding while
 * still providing something that is useful.
 *
 * <p>{@link StreamTable} works well on tables which are regular
 * i.e. same number of columns ({@code td}) in every row and which DO use:
 *
 * <ul>
 * <li>{@code thead}</li>
 * <li>{@code tbody}</li>
 * </ul>
 *
 * <p>{@link StreamTable} works best on tables which DO NOT use:
 *
 * <ul>
 * <li>{@code rowspan}</li>
 * <li>{@code colspan}</li>
 * <li>{@code tfoot}</li>
 * </ul>
 *
 * <p>If any of these assumptions are invalid for your table then {@link StreamTable}
 * might still be useful but use the APi with caution.
 */
public class StreamTable extends HtmlElement {

    @FindBy(css = "thead > tr > th")
    private List<WebElement> headerCells;

    @FindBy(css = "tbody > tr")
    private List<WebElement> rows;

    /**
     * @return a {@link Stream} of visible (i.e. displayed) {@code th} WebElements.
     */
    public Stream<WebElement> getHeadings() {
        return headerCells.stream()
                .filter(WebElement::isDisplayed);
    }

    /**
     * @param index 0-based index
     * @return Optional of the {@code th} specified by the index
     */
    public Optional<WebElement> getHeading(int index) {
        return getHeadings().skip(index).findFirst();
    }

    /**
     * @param text text of the header cell to return
     * @return Optional of the first {@code th} matching {@code text}
     */
    public Optional<WebElement> getHeading(String text) {
        return getHeadings()
                .filter(e -> Objects.equals(e.getText().trim(), text))
                .findFirst();
    }

    /**
     * @param headerMatcher matcher for of the header cell to return
     * @return Optional of the first {@code th} matching {@code headerMatcher}
     */
    public Optional<WebElement> getHeading(Predicate<WebElement> headerMatcher) {
        return getHeading(getHeaderIndex(headerMatcher));
    }

    /**
     * @return {@link Stream} of {@link Stream} of {@code td} {@link WebElement}s
     */
    public Stream<Stream<WebElement>> getRows() {
        return rows.stream()
                .map(el -> el.findElements(By.cssSelector("td")).stream());
    }

    /**
     * @param index 0-based index
     * @return the row specified by the index
     */
    public Optional<List<WebElement>> getRow(int index) {
        return getRowList().skip(index).findFirst();
    }

    public Stream<List<WebElement>> getRowList() {
        return getRows()
                .map(row -> row.filter(WebElement::isDisplayed).collect(toList()))
                .filter(row -> !row.isEmpty());
    }

    /**
     * Can be used where the index is already known or in other cases where the
     * other methods do not work as expected on tables which violate assumptions.
     *
     * @param index 0-based index of the column to return
     * @return a stream of {@code td} tags in the table column {@code index}
     */
    public Stream<WebElement> getColumn(int index) {
        return getRows()
                .map(cells -> cells
                        .filter(WebElement::isDisplayed)
                        .skip(index)
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException(
                                "A row doesn't have column index " + index)));
    }

    /**
     * Returns the first column given a String that matches the text of a header {@code th}.
     * <pre>
     * +--+------------+----+
     * |  | headerText |    |
     * +--+------------+----+
     * |  |      |     |    |
     * |  |   return   |    |
     * |  |      ˅     |    |
     * +--+------------+----+
     * </pre>
     *
     * @param headerText the text of the header we are looking for
     * @return the first column which matches the {@code headerText}
     */
    public Stream<WebElement> getColumn(String headerText) {
        int index = getHeaderIndex(e -> Objects.equals(e.getText(), headerText));
        return getColumn(index);
    }

    /**
     * Returns column given a predicate that matches a header.
     * <pre>
     * +--+-------------+----+
     * |  | headerMatch |    |
     * +--+-------------+----+
     * |  |      |      |    |
     * |  |   return    |    |
     * |  |      ˅      |    |
     * +--+-------------+----+
     * </pre>
     *
     * @param headerMatcher the predicate to test the header text that we are looking for
     * @return the column or range which matches the {@code headerMatcher}
     */
    public Stream<WebElement> getColumn(Predicate<WebElement> headerMatcher) {
        return getColumn(getHeaderIndex(headerMatcher));
    }

    /**
     * N.B. trims the text from the cells before comparing.
     *
     * @param lookupColHeaderText the String to match the header where we want to lookup using {@code lookupCellText}
     * @param lookupCellText      the String to match the to look up for a match in the lookupColHeaderText
     * @param targetColHeaderText the String to match the header containing the return value
     * @return all WebElements from targetColHeaderText which matches {@code lookupCellText} in lookupColHeaderText
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
     * Returns WebElements from target cells given a match in the lookup column
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
     * @param lookupHeaderMatcher the predicate to test the range where we want to lookup
     * @param lookupCellMatcher   the predicate to test the to look up for a match in the lookupRange
     * @param targetHeaderMatcher the predicate to test the range containing the return value
     * @return WebElements in the targetRange which matches {@code lookupMatcher} in lookupRange
     */
    public Stream<WebElement> getCellsByLookup(
            Predicate<WebElement> lookupHeaderMatcher,
            Predicate<WebElement> lookupCellMatcher,
            Predicate<WebElement> targetHeaderMatcher) {

        int lookupColumnIndex = getHeaderIndex(lookupHeaderMatcher);
        int targetColumnIndex = getHeaderIndex(targetHeaderMatcher);

        return getRowList()
                .filter(row -> lookupCellMatcher.test(row.get(lookupColumnIndex)))
                .map(row -> row.get(targetColumnIndex));
    }

    /**
     * Returns index of a header matching the {@code headerPredicate}.
     * This shouldn't be needed outside this class.
     * If you think it should be, file a bug.
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
