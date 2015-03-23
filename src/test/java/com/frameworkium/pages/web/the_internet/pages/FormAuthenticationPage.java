package com.frameworkium.pages.web.the_internet.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.PageFactory;
import com.frameworkium.pages.internal.Visible;

public class FormAuthenticationPage extends BasePage<FormAuthenticationPage> {

    @Visible
    @Name("Username field")
    @FindBy(css = "input#username")
    private WebElement usernameField;

    @Visible
    @Name("Password field")
    @FindBy(css = "input#password")
    private WebElement passwordField;

    @Visible
    @Name("Login button")
    @FindBy(xpath = "//button[contains(.,'Login')]")
    private WebElement loginButton;

    @Step("Successfully Log in")
    public FormAuthenticationSuccessPage validLogin(String username, String password) {	
    	
    	usernameField.clear();
    	usernameField.sendKeys(username);
    	
    	passwordField.clear();
    	passwordField.sendKeys(password);
    	
    	loginButton.click();
    	
        return PageFactory.newInstance(FormAuthenticationSuccessPage.class);
    }

   
}
