package com.heroku.theinternet.pages.web;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.Visible;

public class KeyPressesPage extends BasePage<KeyPressesPage> {

    @Visible
    @Name("Container")
    @FindBy(css = "div.example")
    private WebElement container;

    @Name("Result")
    @FindBy(css = "p#result")
    private WebElement result;

    @Step("Enter a key press {0}")
    public KeyPressesPage enterKeyPress(Keys key) {

        // Press a key
        (new Actions(driver)).sendKeys(key).perform();

        // We're still on this page, so return this
        return this;
    }

    @Step("Get result text")
    public String getResultText() {
        return result.getText();
    }

}
