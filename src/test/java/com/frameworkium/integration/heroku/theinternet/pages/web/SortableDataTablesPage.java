package com.frameworkium.integration.heroku.theinternet.pages.web;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Table;

import java.util.List;

public class SortableDataTablesPage extends BasePage<SortableDataTablesPage> {

    @Visible
    @Name("Heading")
    @FindBy(css = "div.example h3")
    private WebElement heading;

    @Visible
    @Name("Table 1")
    @FindBy(css = "table#table1")
    private Table table1;

    @Visible
    @Name("Table 2")
    @FindBy(css = "table#table2")
    private Table table2;

    @Step("Get table 1 column {0} contents")
    public List<String> getTable1ColumnContents(String colHeader) {
        return getColumnContents(table1, colHeader);
    }

    @Step("Get table 2 column {0} contents")
    public List<String> getTable2ColumnContents(String colHeader) {
        return getColumnContents(table2, colHeader);
    }

    @Step("Sort table 2 by column with header {0}")
    public SortableDataTablesPage sortTable2ByColumnName(String colHeader) {
        sortTableByColumnName(table2, colHeader);
        return this;
    }

    @Step("Sort table {0} by column name {1}")
    private SortableDataTablesPage sortTableByColumnName(Table table, String colHeader) {
        table.getHeadings()
                .get(table.getHeadingsAsString().indexOf(colHeader))
                .click();
        return this;
    }

    @Step("Get column contents of column {1} in table {0}")
    private List<String> getColumnContents(Table table, String colHeader) {

        int colIndex = table.getHeadingsAsString().indexOf(colHeader);
        return table.getColumnsAsString().get(colIndex);
    }

}
