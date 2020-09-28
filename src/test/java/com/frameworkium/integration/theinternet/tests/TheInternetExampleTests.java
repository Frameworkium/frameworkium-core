package com.frameworkium.integration.theinternet.tests;

import com.frameworkium.core.ui.tests.BaseUITest;
import com.frameworkium.integration.theinternet.pages.*;
import com.google.common.truth.Truth8;
import org.openqa.selenium.Keys;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

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
        Truth8.assertThat(checkboxesStatus).doesNotContain(false);
    }

    public void dragAndDrop() {

        List<String> headings = WelcomePage.open()
                .clickDragAndDropLink()
                .dragAontoB()
                .getListOfHeadings();

        assertWithMessage("Order of headings")
                .that(headings)
                .containsExactly("B", "A");
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
                .isEqualTo("You successfuly clicked an alert");
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

        Truth8.assertThat(tablesPage.getTable1ColumnContents("Web Site"))
                .contains("http://www.jdoe.com");

        List<String> sortedLastNameColumn =
                tablesPage.sortTable2ByColumnName("Last Name")
                        .getTable2ColumnContents("Last Name")
                        .collect(Collectors.toList());

        assertThat(sortedLastNameColumn.get(0)).isEqualTo("Bach");
        assertThat(sortedLastNameColumn).isInOrder();
    }

}
