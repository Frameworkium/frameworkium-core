package com.frameworkium.integration.heroku.theinternet.pages.web;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import com.frameworkium.core.ui.pages.PageFactory;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Link;

public class WelcomePage extends BasePage<WelcomePage> {

    @Visible
    @Name("Basic auth link")
    @FindBy(linkText = "Basic Auth")
    private Link basicAuthLink;

    @Name("Checkboxes link")
    @FindBy(linkText = "Checkboxes")
    private Link checkboxesLink;

    @Name("Drag and Drop link")
    @FindBy(linkText = "Drag and Drop")
    private Link dragAndDropLink;

    @Name("Dynamic Loading link")
    @FindBy(linkText = "Dynamic Loading")
    private Link dynamicLoadingLink;

    @Name("File Upload Link")
    @FindBy(linkText = "File Upload")
    private Link fileUploadLink;

    @Name("Hovers Link")
    @FindBy(linkText = "Hovers")
    private Link hoversLink;
    
    @Name("Frames Link")
    @FindBy(linkText = "Frames")
    private Link framesLink;

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

    @Step("Click the Dynamic Loading link - user: {0}, password {1}")
    public BasicAuthSuccessPage clickBasicAuth(String username, String password) {
        // For this sort of authentication, Selenium cannot handle the dialog
        // box that appears if you click the link.
        // Instead, we can provide the username and password in the URL:
        String url = formatBasicAuthURL(username, password, basicAuthLink.getReference());
        return PageFactory.newInstance(BasicAuthSuccessPage.class, url);
    }

    @Step("Click the Checkboxes link")
    public CheckboxesPage clickCheckboxesLink() {
        checkboxesLink.click();
        return PageFactory.newInstance(CheckboxesPage.class);
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

    @Step("Click the Frames link")
    public FramesPage clickFramesLink() {
        framesLink.click();
        return PageFactory.newInstance(FramesPage.class);
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

    private String formatBasicAuthURL(String username, String password, String originalURL) {
        return String.format("http://%s:%s@%s",
                username, password, originalURL.replace("http://", ""));
    }
}
