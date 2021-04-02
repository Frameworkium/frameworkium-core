package com.frameworkium.integration.theinternet.pages;

import com.frameworkium.lite.htmlelements.annotations.Timeout;
import com.frameworkium.lite.htmlelements.element.Button;
import com.frameworkium.lite.htmlelements.element.HtmlElement;
import com.frameworkium.lite.ui.annotations.Invisible;
import com.frameworkium.lite.ui.annotations.Visible;
import com.frameworkium.lite.ui.pages.BasePage;
import org.openqa.selenium.support.FindBy;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

public class DynamicLoadingExamplePage extends BasePage<DynamicLoadingExamplePage> {

    @Visible
    @FindBy(css = "#start button")
    private Button startButton;

    @Invisible
    @Timeout(0) // prevents page load taking 10s due to implicit timeout
    @FindBy(id = "finish")
    private HtmlElement dynamicElement;

    public static DynamicLoadingExamplePage open() {
        return new DynamicLoadingExamplePage()
                .get("https://the-internet.herokuapp.com/dynamic_loading/2");
    }

    public DynamicLoadingExamplePage clickStart() {
        startButton.click();
        return this;
    }

    public String getElementText() {
        wait.until(visibilityOf(dynamicElement));
        return dynamicElement.getText();
    }

}
