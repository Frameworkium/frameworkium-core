package com.frameworkium.integration.theinternet.pages;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Button;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

public class JavaScriptAlertsPage extends BasePage<JavaScriptAlertsPage> {

    @Visible
    @Name("JS Alert button")
    @FindBy(css = "button[onclick='jsAlert()']")
    private Button jsAlertButton;

    @Name("Result area")
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
