package com.frameworkium.integration.heroku.theinternet.tests.web;

import com.frameworkium.core.ui.tests.BaseTest;
import com.frameworkium.integration.heroku.theinternet.pages.web.*;
import org.openqa.selenium.Keys;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Issue;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class TheInternetExampleTests extends BaseTest {

    @Issue("HEROKU-2")
    @Test(description = "Checkboxes")
    public void checkBoxes() {

        CheckboxesPage checkboxesPage = WelcomePage.open(15).then()
                // Navigate to the checkboxes page
                .clickCheckboxesLink()
                // Set all checkboxes to checked via alternative method
                .checkAllCheckboxes();

        // Assert that all checkboxes are checked
        assertThat(checkboxesPage.getAllCheckboxCheckedStatus())
                .named("check status of checkboxes")
                .doesNotContain(false);
    }

    @Issue("HEROKU-3")
    @Test(description = "Drag and Drop")
    public void dragAndDrop() {

        // Navigate to the checkboxes page
        List<String> headings = WelcomePage.open().then()
                .clickDragAndDropLink()
                // Drag A onto B
                .dragAontoB()
                .getListOfHeadings();

        // Assert on the order of the headings
        assertThat(headings)
                .named("Order of headings")
                .containsExactly("B", "A");
    }

    @Issue("HEROKU-5")
    @Test(description = "Dynamic loading")
    public void dynamicLoading() {

        // Navigate to the dynamic loading hidden element page
        DynamicLoadingExamplePage dynamicLoadingExamplePage =
                WelcomePage.open().then().clickDynamicLoading().then().clickExample1();

        // Click start and wait for element to be displayed
        dynamicLoadingExamplePage.clickStart().then().waitForElementToBeDisplayed();

        // Assert that the element is indeed displayed
        assertThat(dynamicLoadingExamplePage.isElementDisplayed())
                .named("element visibility")
                .isTrue();

        // Navigate to the dynamic loading hidden element page
        dynamicLoadingExamplePage =
                WelcomePage.open().then().clickDynamicLoading().then().clickExample2();

        // Click start and wait for element to be displayed
        dynamicLoadingExamplePage.clickStart().then().waitForElementToBeDisplayed();

        // Assert that the element is indeed displayed
        assertThat(dynamicLoadingExamplePage.isElementDisplayed())
                .named("element visibility")
                .isTrue();
    }

    @Issue("HEROKU-9")
    @Test(description = "Hovers")
    public void hovers() {

        // Navigate to the hovers page
        HoversPage hoversPage = WelcomePage.open().then().clickHoversLink();

        // Confirm that the caption under the first figure contains expected text
        assertThat(hoversPage.getFirstFigureCaption()).contains("name: user1");
    }

    @Issue("HEROKU-11")
    @Test(description = "Javascript Alerts")
    public void javascriptAlerts() {

        // Navigate to the javascript alerts page
        JavaScriptAlertsPage javascriptAlerts =
                WelcomePage.open().then()
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

    @Issue("HEROKU-14")
    @Test(description = "Table Manipulation & Validation")
    public void sortDataTable() {

        // Navigate to the sortable data tables page
        SortableDataTablesPage sortableDataTablesPage =
                WelcomePage.open().then().clickSortableDataTablesLink();

        // Assert that Table 1 contains "http://www.jdoe.com" in the web site column
        assertThat(sortableDataTablesPage.getTable1ColumnContents("Web Site"))
                .contains("http://www.jdoe.com");

        // Sort Table 2 by last name column
        sortableDataTablesPage.sortTable2ByColumnName("Last Name");

        List<String> lastNameColumn =
                sortableDataTablesPage.getTable2ColumnContents("Last Name");

        // Confirm that the column is then ordered by the last name
        assertThat(lastNameColumn).isOrdered();

        // Confirm that "Bach" is then the first surname in table 2
        assertThat(lastNameColumn.get(0)).isEqualTo("Bach");
    }
}
