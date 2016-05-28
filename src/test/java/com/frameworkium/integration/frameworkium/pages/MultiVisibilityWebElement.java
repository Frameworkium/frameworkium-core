package com.frameworkium.integration.frameworkium.pages;

import com.frameworkium.core.ui.annotations.Invisible;
import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import com.frameworkium.core.ui.pages.PageFactory;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class MultiVisibilityWebElement
        extends BasePage<MultiVisibilityWebElement> {

    @Visible
    @Invisible
    @FindBy(css = "body")
    private WebElement webElement;

    public static MultiVisibilityWebElement open() {
        return PageFactory.newInstance(MultiVisibilityWebElement.class);
    }
}
