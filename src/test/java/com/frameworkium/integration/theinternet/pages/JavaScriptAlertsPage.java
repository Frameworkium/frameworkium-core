package com.frameworkium.integration.theinternet.pages;

import com.frameworkium.core.htmlelements.element.Button;
import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

public class JavaScriptAlertsPage extends BasePage<JavaScriptAlertsPage> {

    @Visible
    @FindBy(css = "button[onclick='jsAlert()']")
    private Button jsAlertButton;

    @FindBy(css = "p#result")
    private WebElement resultArea;

    @Step("Click alert")
    public JavaScriptAlertsPage clickAlertButtonAndAccept() {
        jsAlertButton.click();
        driver.switchTo().alert().accept();
        wait.until(visibilityOf(resultArea));
        return this;
    }

    @Step("Click prompt")
    public String getResultText() {
        return resultArea.getText();
    }
}
