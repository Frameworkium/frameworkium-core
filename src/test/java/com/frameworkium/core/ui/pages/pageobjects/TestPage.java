package com.frameworkium.core.ui.pages.pageobjects;

import com.frameworkium.core.ui.pages.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Java class to test page instantiation.
 */
public class TestPage extends BasePage<TestPage> {

    @FindBy(css = "#foo")
    private WebElement element;

    public boolean isWebElementNull() {
        return element == null;
    }
}
