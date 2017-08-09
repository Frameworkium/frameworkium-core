package com.frameworkium.integration.github.pages.web;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import com.frameworkium.core.ui.pages.PageFactory;
import com.frameworkium.integration.github.pages.web.components.HeaderComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.allure.annotations.Step;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class HomePage extends BasePage<HomePage> {

    @Visible
    private HeaderComponent header;

    @Visible
    @FindBy(css = "form[class*='signup'] button")
    private WebElement signUpButton;

    @Step("Navigate to the Github homepage")
    public static HomePage open() {
        return PageFactory.newInstance(HomePage.class, "https://github.com");
    }

    public HeaderComponent theHeader() {
        return header;
    }

    public void waitForSomething() {
        wait.until(visibilityOfElementLocated(By.cssSelector("body")));
    }
}
