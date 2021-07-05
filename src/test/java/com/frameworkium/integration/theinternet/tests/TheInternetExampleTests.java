package com.frameworkium.integration.theinternet.tests;

import com.frameworkium.integration.theinternet.pages.*;
import com.frameworkium.lite.ui.tests.BaseUITest;
import org.openqa.selenium.Keys;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Test
public class TheInternetExampleTests extends BaseUITest {

    public void check_boxes_can_all_be_checked() {

        int timeout = 15;
        List<Boolean> checkboxesStatus =
                WelcomePage.open(timeout)
                        .clickCheckboxesLink()
                        .checkAllCheckboxes()
                        .getAllCheckboxCheckedStatus()
                        .collect(Collectors.toList());

        // Assert that all checkboxes are checked
        assertThat(checkboxesStatus).isNotEmpty();
        assertThat(checkboxesStatus).doesNotContain(false);
    }

    public void dragAndDrop() {

        var headings = WelcomePage.open()
                .clickDragAndDropLink()
                .dragAontoB()
                .getListOfHeadings();

        assertThat(headings).containsExactly("B", "A");
    }

    public void dynamicLoading() {

        String elementText =
                DynamicLoadingExamplePage
                        .open()
                        .clickStart()
                        .getElementText();

        assertThat(elementText).isEqualTo("Hello World!");
    }

    public void dropdowns() {
        var dropDownPage = WelcomePage.open()
                .clickDropDownLink();

        assertThat(dropDownPage.getAllOptions())
                .isEqualTo(List.of("Please select an option", "Option 1", "Option 2"));

        dropDownPage.select("Option 1");

        assertThat(dropDownPage.getCurrentSelection()).isEqualTo("Option 1");
    }

    public void hovers() {

        String firstFigureCaption = WelcomePage.open()
                .clickHoversLink()
                .getFirstFigureCaption();

        assertThat(firstFigureCaption).contains("name: user1");
    }

    public void javascriptAlerts() {

        var javascriptAlerts =
                WelcomePage.open()
                        .clickJavascriptAlertsLink()
                        .clickAlertButtonAndAccept();

        assertThat(javascriptAlerts.getResultText())
                .isEqualTo("You successfully clicked an alert");
    }

    public void keyPresses() {

        var keyPressesPage = WelcomePage
                .open()
                .clickKeyPressesLink()
                .enterKeyPress(Keys.ENTER);

        assertThat(keyPressesPage.getResultText())
                .isEqualTo("You entered: " + Keys.ENTER.name());
    }

    public void sortDataTable() {

        var tablesPage = SortableDataTablesPage.open();

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
