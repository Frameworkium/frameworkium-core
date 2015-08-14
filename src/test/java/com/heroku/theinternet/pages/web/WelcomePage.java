package com.heroku.theinternet.pages.web;

import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Link;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.PageFactory;
import com.frameworkium.pages.internal.Visible;

public class WelcomePage extends BasePage<WelcomePage> {

    @Visible
    @Name("Basic auth link")
    @FindBy(linkText = "Basic Auth")
    private Link basicAuthLink;

    @Visible
    @Name("Checkboxes link")
    @FindBy(linkText = "Checkboxes")
    private Link checkboxesLink;

    @Visible
    @Name("Drag and Drop link")
    @FindBy(linkText = "Drag and Drop")
    private Link dragAndDropLink;

    @Visible
    @Name("Dropdown Link")
    @FindBy(linkText = "Dropdown")
    private Link dropdownLink;

    @Visible
    @Name("Dynamic Loading link")
    @FindBy(linkText = "Dynamic Loading")
    private Link dynamicLoadingLink;

    @Visible
    @Name("File Download Link")
    @FindBy(linkText = "File Download")
    private Link fileDownloadLink;

    @Visible
    @Name("File Upload Link")
    @FindBy(linkText = "File Upload")
    private Link fileUploadLink;

    @Visible
    @Name("Form Authentication Link")
    @FindBy(linkText = "Form Authentication")
    private Link formAuthenticationLink;

    @Visible
    @Name("Hovers Link")
    @FindBy(linkText = "Hovers")
    private Link hoversLink;
    
    @Visible
    @Name("Frames Link")
    @FindBy(linkText = "Frames")
    private Link framesLink;

    @Visible
    @Name("JQuery UI Link")
    @FindBy(linkText = "JQuery UI Menus")
    private Link jqueryUILink;

    @Visible
    @Name("JavaScript Alerts Link")
    @FindBy(linkText = "JavaScript Alerts")
    private Link javascriptAlertsLink;

    @Visible
    @Name("Key Presses Link")
    @FindBy(linkText = "Key Presses")
    private Link keyPressesLink;

    @Visible
    @Name("Secure File Download Link")
    @FindBy(linkText = "Secure File Download")
    private Link secureFileDownloadLink;

    @Visible
    @Name("Sortable Data Tables Link")
    @FindBy(linkText = "Sortable Data Tables")
    private Link sortableDataTablesLink;

    @Step("Navigate to http://the-internet.herokuapp.com")
    public static WelcomePage open() {
        return PageFactory.newInstance(WelcomePage.class, "http://the-internet.herokuapp.com");
    }
}
