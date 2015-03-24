package com.github.pages.web;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.PageFactory;
import com.frameworkium.pages.internal.Visible;
import com.github.pages.web.components.HeaderComponent;

public class HomePage extends BasePage<HomePage> {

    @Name("Header")
    @Visible
    private HeaderComponent header;

    @Name("Marketing Sign-Up Banner")
    @Visible
    @FindBy(css = "div.marketing-section-signup")
    private WebElement marketingBanner;

    @Step("Navigate to the Github homepage")
    public static HomePage open() {
        return PageFactory.newInstance(HomePage.class, "http://github.com");
    }

    public HeaderComponent theHeader() {
        return header;
    }
}
