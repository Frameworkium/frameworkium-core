package com.frameworkium.integration.theinternet.tests;

import com.frameworkium.lite.ui.tests.BaseUITest;
import com.frameworkium.integration.theinternet.pages.*;
import org.openqa.selenium.Keys;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Test
public class TheInternetExampleTests extends BaseUITest {

    public void check_boxes_can_all_be_checked() {

        final int timeout = 15;
        Stream<Boolean> checkboxesStatus =
                WelcomePage.open(timeout)
                        .clickCheckboxesLink()
                        .checkAllCheckboxes()
                        .getAllCheckboxCheckedStatus();

        // Assert that all checkboxes are checked
        assertThat(checkboxesStatus).doesNotContain(false);
    }

    public void dragAndDrop() {

        List<String> headings = WelcomePage.open()
                .clickDragAndDropLink()
                .dragAontoB()
                .getListOfHeadings();

        assertThat(headings).containsExactly("B", "A");
    }

    public void dynamicLoading() {

        String elementText =
                DynamicLoadingExamplePage
                        .openExampleTwo()
                        .clickStart()
                        .getElementText();

        assertThat(elementText).isEqualTo("Hello World!");
    }

    public void hovers() {

        String firstFigureCaption = WelcomePage.open()
                .clickHoversLink()
                .getFirstFigureCaption();

        assertThat(firstFigureCaption).contains("name: user1");
    }

    public void javascriptAlerts() {

        JavaScriptAlertsPage javascriptAlerts =
                WelcomePage.open()
                        .clickJavascriptAlertsLink()
                        .clickAlertButtonAndAccept();

        assertThat(javascriptAlerts.getResultText())
                .isEqualTo("You successfully clicked an alert");
    }

    public void keypresses() {

        KeyPressesPage keyPressesPage = WelcomePage
                .open()
                .clickKeyPressesLink()
                .enterKeyPress(Keys.ENTER);

        assertThat(keyPressesPage.getResultText())
                .isEqualTo("You entered: " + Keys.ENTER.name());
    }

    public void sortDataTable() {

        SortableDataTablesPage tablesPage = SortableDataTablesPage.open();

        assertThat(tablesPage.getTable1ColumnContents("Web Site"))
                .contains("http://www.jdoe.com");

        List<String> sortedLastNameColumn =
                tablesPage.sortTable2ByColumnName("Last Name")
                        .getTable2ColumnContents("Last Name")
                        .collect(Collectors.toList());

        assertThat(sortedLastNameColumn.get(0)).isEqualTo("Bach");
        assertThat(sortedLastNameColumn).isSorted();
    }

}
