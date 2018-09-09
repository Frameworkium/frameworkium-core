package com.frameworkium.integration.heroku.theinternet.tests;

import com.frameworkium.core.ui.tests.BaseUITest;
import com.frameworkium.integration.heroku.theinternet.pages.*;
import com.google.common.truth.Truth8;
import io.qameta.allure.*;
import org.openqa.selenium.Keys;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;

@Feature("The Internet Example Feature")
public class TheInternetExampleTests extends BaseUITest {

    @Issue("HEROKU-2")
    @Story("Check boxes story")
    @Test(description = "Checkboxes")
    public void checkBoxes() {

        final int timeout = 15;
        CheckboxesPage checkboxesPage = WelcomePage.open(timeout)
                .clickCheckboxesLink()
                .checkAllCheckboxes();

        // Assert that all checkboxes are checked
        assertThat(checkboxesPage.getAllCheckboxCheckedStatus())
                .named("check status of checkboxes")
                .doesNotContain(false);
    }

    @Issue("HEROKU-3")
    @Test(description = "Drag and Drop")
    public void dragAndDrop() {

        List<String> headings = WelcomePage.open()
                .clickDragAndDropLink()
                .dragAontoB()
                .getListOfHeadings();

        assertThat(headings)
                .named("Order of headings")
                .containsExactly("B", "A");
    }

    @Issue("HEROKU-5")
    @Test(description = "Dynamic loading")
    public void dynamicLoading() {

        DynamicLoadingExamplePage dynamicLoadingPage =
                WelcomePage.open()
                        .clickDynamicLoading()
                        .clickExample2()
                        .clickStart().
                        waitForElementToBeDisplayed();

        // Assert that the element is indeed displayed
        assertThat(dynamicLoadingPage.isElementDisplayed())
                .named("element visibility")
                .isTrue();
    }

    @Issue("HEROKU-9")
    @Test(description = "Hovers")
    public void hovers() {

        // Navigate to the hovers page
        HoversPage hoversPage = WelcomePage.open().clickHoversLink();

        // Confirm that the caption under the first figure contains expected text
        assertThat(hoversPage.getFirstFigureCaption()).contains("name: user1");

    }

    @Issue("HEROKU-11")
    @Test(description = "Javascript Alerts")
    public void javascriptAlerts() {

        // Navigate to the javascript alerts page
        JavaScriptAlertsPage javascriptAlerts =
                WelcomePage.open()
                        .clickJavascriptAlertsLink()
                        .clickAlertButtonAndAccept();

        assertThat(javascriptAlerts.getResultText())
                .isEqualTo("You successfuly clicked an alert");
    }

    @Issue("HEROKU-12")
    @Test(description = "Key Presses")
    public void keypresses() {

        // Navigate to the key presses page
        KeyPressesPage keyPressesPage = WelcomePage
                .open()
                .clickKeyPressesLink()
                .enterKeyPress(Keys.ENTER);

        assertThat(keyPressesPage.getResultText())
                .isEqualTo("You entered: " + Keys.ENTER.name());
    }

    @Issue("INT-14")
    public void sortDataTable() {

        SortableDataTablesPage tablesPage = SortableDataTablesPage.open();

        Truth8.assertThat(tablesPage.getTable1ColumnContents("Web Site"))
                .contains("http://www.jdoe.com");

        List<String> sortedLastNameColumn =
                tablesPage.sortTable2ByColumnName("Last Name")
                        .getTable2ColumnContents("Last Name")
                        .collect(Collectors.toList());

        assertThat(sortedLastNameColumn.get(0)).isEqualTo("Bach");
        assertThat(sortedLastNameColumn).isOrdered();
    }

}
