package com.github.pages.web;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.PageFactory;
import com.frameworkium.pages.internal.Visible;
import com.github.pages.web.components.HeaderComponent;

public class ExplorePage extends BasePage<ExplorePage> {

    @Name("Header")
    @Visible
    private HeaderComponent header;

    @Name("Explore Content")
    @Visible
    @FindBy(css = "div.explore-pjax-container.explore-page.js-explore-page")
    private WebElement marketingBanner;

    @Step("Navigate to the Github homepage")
    public static ExplorePage open() {
        return PageFactory.newInstance(ExplorePage.class, "http://github.com");
    }

    public HeaderComponent theHeader() {
        return header;
    }
}
