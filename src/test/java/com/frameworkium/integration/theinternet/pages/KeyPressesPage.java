package com.frameworkium.integration.theinternet.pages;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

public class KeyPressesPage extends BasePage<KeyPressesPage> {

    @Visible
    @FindBy(css = "div.example")
    private WebElement container;

    @FindBy(css = "p#result")
    private WebElement result;

    @Step("Enter a key press {key}")
    public KeyPressesPage enterKeyPress(Keys key) {
        (new Actions(driver)).sendKeys(key).perform();
        return this;
    }

    @Step("Get result text")
    public String getResultText() {
        return result.getText();
    }

}
