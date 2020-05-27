package com.frameworkium.integration.frameworkium.pages;

import com.frameworkium.lite.ui.ExtraExpectedConditions;
import com.frameworkium.lite.ui.annotations.Visible;
import com.frameworkium.lite.ui.pages.BasePage;
import com.frameworkium.lite.ui.pages.PageFactory;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class JQueryDemoPage extends BasePage<JQueryDemoPage> {

    @Visible
    @FindBy(css = "#content > h1")
    private WebElement heading;

    public static JQueryDemoPage open() {
        return PageFactory.newInstance(
                JQueryDemoPage.class,
                "https://jqueryui.com/demos/");
    }

    public JQueryDemoPage waitForJQuery() {
        // Just testing it doesn't fail
        wait.until(ExtraExpectedConditions.jQueryAjaxDone());
        return this;
    }

    public String getHeadingText() {
        return heading.getText();
    }
}
