package com.heroku.theinternet.pages.web;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.Visible;

public class JavaScriptAlertsPage extends BasePage<JavaScriptAlertsPage> {

    @Visible
    @Name("JS Alert button")
    @FindBy(css = "button[onclick='jsAlert()']")
    private WebElement jsAlertButton;

    @Visible
    @Name("JS Confirm button")
    @FindBy(css = "button[onclick='jsConfirm()']")
    private WebElement jsConfirmButton;

    @Visible
    @Name("JS Prompt button")
    @FindBy(css = "button[onclick='jsPrompt()']")
    private WebElement jsPromptButton;

    @Name("Result area")
    @FindBy(css = "p#result")
    private WebElement resultArea;

    @Step("Click alert")
    public JavaScriptAlertsPage clickAlertButtonAndAccept() {
        jsAlertButton.click();

        driver.switchTo().alert().accept();

        wait.until(ExpectedConditions.visibilityOf(resultArea));

        return this;
    }

    @Step("Click alert")
    public JavaScriptAlertsPage clickAlertButtonAndDismiss() {
        jsAlertButton.click();

        driver.switchTo().alert().dismiss();

        wait.until(ExpectedConditions.visibilityOf(resultArea));

        return this;
    }

    @Step("Click confirm and confirm")
    public JavaScriptAlertsPage clickConfirmButtonAndAccept() {
        jsConfirmButton.click();

        driver.switchTo().alert().accept();

        wait.until(ExpectedConditions.visibilityOf(resultArea));

        return this;
    }

    @Step("Click confirm and dismiss")
    public JavaScriptAlertsPage clickConfirmButtonAndDismiss() {
        jsConfirmButton.click();

        driver.switchTo().alert().dismiss();

        wait.until(ExpectedConditions.visibilityOf(resultArea));

        return this;
    }

    @Step("Click prompt")
    public JavaScriptAlertsPage clickPromptButtonAndEnterPrompt(String textToEnter) {
        jsPromptButton.click();

        driver.switchTo().alert().sendKeys(textToEnter);
        driver.switchTo().alert().accept();

        return this;
    }

    @Step("Click prompt")
    public String getResultText() {
        return resultArea.getText();
    }
}
