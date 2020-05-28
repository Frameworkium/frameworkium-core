package com.frameworkium.integration.theinternet.pages;

import com.frameworkium.lite.ui.annotations.Visible;
import com.frameworkium.lite.ui.pages.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.frameworkium.lite.htmlelements.element.Button;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

public class JavaScriptAlertsPage extends BasePage<JavaScriptAlertsPage> {

    @Visible
    @FindBy(css = "button[onclick='jsAlert()']")
    private Button jsAlertButton;

    @FindBy(css = "p#result")
    private WebElement resultArea;

    public JavaScriptAlertsPage clickAlertButtonAndAccept() {
        jsAlertButton.click();
        driver.switchTo().alert().accept();
        wait.until(visibilityOf(resultArea));
        return this;
    }

    public String getResultText() {
        return resultArea.getText();
    }
}
