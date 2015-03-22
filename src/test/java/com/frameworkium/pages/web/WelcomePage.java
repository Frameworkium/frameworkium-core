package com.frameworkium.pages.web;

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

    @Step("Navigate to http://the-internet.herokuapp.com")
    public static WelcomePage open() {
        return PageFactory.newInstance(WelcomePage.class, "http://the-internet.herokuapp.com");
    }

    @Step("Click the Dynamic Loading link - user: {0}, password {1}")
    public BasicAuthSuccessPage clickBasicAuth(String username, String password) {
        // For this sort of authentication, Selenium cannot handle the dialog
    	// box that appears if you click the link.
    	// Instead, we can provide the username and password in the URL:
        return PageFactory.newInstance(BasicAuthSuccessPage.class, "http://" + username + ":"
                + password + "@" + basicAuthLink.getReference().split("http://")[1]);
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

    @Step("Click the Dropdown link")
    public DropdownPage clickDropdownLink() {
        dropdownLink.click();
        return PageFactory.newInstance(DropdownPage.class);
    }

    @Step("Click the Dynamic Loading link")
    public DynamicLoadingPage clickDynamicLoading() {
        dynamicLoadingLink.click();
        return PageFactory.newInstance(DynamicLoadingPage.class);
    }

    @Step("Click the File Download link")
    public FileDownloadPage clickFileDownloadLink() {
        fileDownloadLink.click();
        return PageFactory.newInstance(FileDownloadPage.class);
    }

    @Step("Click the File Upload link")
    public FileUploadPage clickFileUploadLink() {
        fileUploadLink.click();
        return PageFactory.newInstance(FileUploadPage.class);
    }

    @Step("Click the Form authentication link")
    public FormAuthenticationPage clickFormAuthenticationLink() {
        formAuthenticationLink.click();
        return PageFactory.newInstance(FormAuthenticationPage.class);
    }

    @Step("Click the Hovers link")
    public HoversPage clickHoversLink() {
        hoversLink.click();
        return PageFactory.newInstance(HoversPage.class);
    }

    @Step("Click the Jquery UI link")
    public JQueryUIMenuPage clickJQueryUILink() {
        jqueryUILink.click();
        return PageFactory.newInstance(JQueryUIMenuPage.class);
    }

    @Step("Click the JavaScript Alerts link")
    public JavaScriptAlertsPage clickjavascriptAlertsLink() {
        javascriptAlertsLink.click();
        return PageFactory.newInstance(JavaScriptAlertsPage.class);
    }

    @Step("Click the Key Presses link")
    public KeyPressesPage clickKeyPressesLink() {
        keyPressesLink.click();
        return PageFactory.newInstance(KeyPressesPage.class);
    }

    @Step("Click the Secure File download link")
    public SecureFileDownloadPage clickSecureFileDownloadsLink(String username, String password) {
        // For this sort of authentication, Selenium cannot handle the dialog
    	// box that appears if you click the link.
    	// Instead, we can provide the username and password in the URL:
    	return PageFactory.newInstance(SecureFileDownloadPage.class,
    			"http://" + username + ":" + password +
    			"@" + secureFileDownloadLink.getReference().split("http://")[1]);
    }

}
