package com.bootstrapium.pages.mobi.components;

import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import com.bootstrapium.pages.internal.PageFactory;
import com.bootstrapium.pages.mobi.HomePage;

public class Header extends HtmlElement {

    @Name("Rightmove logo")
    @FindBy(linkText = "home")
    public Button logo;

    @Step("Click Rightmove logo to return to the home page")
    public HomePage clickLogoToGoHome() {
        logo.click();
        return PageFactory.getInstance(HomePage.class);
    }
}
