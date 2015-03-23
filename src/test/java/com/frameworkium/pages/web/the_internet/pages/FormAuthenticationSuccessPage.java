package com.frameworkium.pages.web.the_internet.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.Visible;

public class FormAuthenticationSuccessPage extends BasePage<FormAuthenticationSuccessPage> {

    @Visible
    @Name("Logout button")
    @FindBy(css = "a[href='/logout']")
    private WebElement logoutButton;

    @Step("Log out")
    public void logout() {
        logoutButton.click();
    }

}
