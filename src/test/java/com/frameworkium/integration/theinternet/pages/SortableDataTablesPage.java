package com.frameworkium.integration.theinternet.pages;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.element.OptimisedStreamTable;
import com.frameworkium.core.ui.pages.BasePage;
import com.frameworkium.core.ui.pages.PageFactory;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

import java.util.stream.Stream;

public class SortableDataTablesPage extends BasePage<SortableDataTablesPage> {

    @Visible
    @CacheLookup
    @FindBy(id = "table1")
    private OptimisedStreamTable table1;

    @CacheLookup
    @FindBy(id = "table2")
    private OptimisedStreamTable table2;

    public static SortableDataTablesPage open() {
        return PageFactory.newInstance(
                SortableDataTablesPage.class,
                "https://the-internet.herokuapp.com/tables");
    }

    public Stream<String> getTable1ColumnContents(String colHeader) {
        return table1.getColumn(colHeader).map(WebElement::getText);
    }

    public Stream<String> getTable2ColumnContents(String colHeader) {
        return table2.getColumn(colHeader).map(WebElement::getText);
    }

    public SortableDataTablesPage sortTable2ByColumnName(String colHeader) {
        table2.getHeading(colHeader).ifPresent(WebElement::click);
        return this;
    }

}
