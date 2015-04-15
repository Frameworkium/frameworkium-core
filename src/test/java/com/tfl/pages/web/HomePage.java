package com.tfl.pages.web;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.PageFactory;
import com.frameworkium.pages.internal.Visible;
import com.tfl.pages.web.components.NavBar;

public class HomePage extends BasePage<HomePage> {

    @Name("Nav Bar")
    @Visible
    private NavBar navBar;

    @Name("Tube Line Status Board")
    @Visible
    @FindBy(css = "div.board-wrapper")
    private WebElement statusBoard;

    @Step("Navigate to the TFL homepage")
    public static HomePage open() {
        return PageFactory.newInstance(HomePage.class, "http://tfl.gov.uk");
    }

    public NavBar theNavBar() {
        return navBar;
    }
}
