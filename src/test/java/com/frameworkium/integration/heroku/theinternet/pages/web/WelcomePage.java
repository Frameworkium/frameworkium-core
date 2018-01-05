package com.frameworkium.integration.heroku.theinternet.pages.web;

import com.frameworkium.core.ui.annotations.ForceVisible;
import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import com.frameworkium.core.ui.pages.PageFactory;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Link;

public class WelcomePage extends BasePage<WelcomePage> {

    @Visible
    @Name("Checkboxes link")
    @FindBy(linkText = "Checkboxes")
    private Link checkboxesLink;

    // ForceVisible not strictly required, just testing it doesn't error
    @ForceVisible
    @Name("Drag and Drop link")
    @FindBy(linkText = "Drag and Drop")
    private Link dragAndDropLink;

    @Name("Dynamic Loading link")
    @FindBy(linkText = "Dynamic Loading")
    private Link dynamicLoadingLink;

    @Name("Hovers Link")
    @FindBy(linkText = "Hovers")
    private Link hoversLink;

    @Name("JavaScript Alerts Link")
    @FindBy(linkText = "JavaScript Alerts")
    private Link javascriptAlertsLink;

    @Name("Key Presses Link")
    @FindBy(linkText = "Key Presses")
    private Link keyPressesLink;

    @Name("Sortable Data Tables Link")
    @FindBy(linkText = "Sortable Data Tables")
    private Link sortableDataTablesLink;

    @Step("Navigate to http://the-internet.herokuapp.com")
    public static WelcomePage open() {
        return PageFactory.newInstance(
                WelcomePage.class, "http://the-internet.herokuapp.com");
    }

    /**
     * Static factory method for this page object with specified timeout.
     *
     * @param timeout timeout in seconds
     * @return An instance of this page object with specified wait timeout
     */
    @Step("Navigate to http://the-internet.herokuapp.com")
    public static WelcomePage open(long timeout) {
        return PageFactory.newInstance(
                WelcomePage.class, "http://the-internet.herokuapp.com", timeout);
    }

    @Step("Click the Checkboxes link")
    public CheckboxesPage clickCheckboxesLink() {
        logger.trace("Showing example use of logger in BasePage");
        // example use of BasePage visibility (not actually required here)
        visibility.forceVisible(checkboxesLink);
        checkboxesLink.click();
        return PageFactory.newInstance(CheckboxesPage.class, 15);
    }

    @Step("Click the Drag And Drop link")
    public DragAndDropPage clickDragAndDropLink() {
        dragAndDropLink.click();
        return PageFactory.newInstance(DragAndDropPage.class);
    }

    @Step("Click the Dynamic Loading link")
    public DynamicLoadingPage clickDynamicLoading() {
        dynamicLoadingLink.click();
        return PageFactory.newInstance(DynamicLoadingPage.class);
    }

    @Step("Click the Hovers link")
    public HoversPage clickHoversLink() {
        hoversLink.click();
        return PageFactory.newInstance(HoversPage.class);
    }

    @Step("Click the JavaScript Alerts link")
    public JavaScriptAlertsPage clickJavascriptAlertsLink() {
        javascriptAlertsLink.click();
        return PageFactory.newInstance(JavaScriptAlertsPage.class);
    }

    @Step("Click the Key Presses link")
    public KeyPressesPage clickKeyPressesLink() {
        keyPressesLink.click();
        return PageFactory.newInstance(KeyPressesPage.class);
    }

    @Step("Click the Sortable Data Table link")
    public SortableDataTablesPage clickSortableDataTablesLink() {
        sortableDataTablesLink.click();
        return PageFactory.newInstance(SortableDataTablesPage.class);
    }
}
