package com.frameworkium.integration.seleniumhq.pages;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import com.frameworkium.core.ui.pages.PageFactory;
import com.frameworkium.integration.seleniumhq.components.HeaderComponent;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage<HomePage> {

    @CacheLookup
    @Visible
    private HeaderComponent header;

    @FindBy(css = ".headerLink + #dropdownButton")
    private WebElement menuLink;

    public static HomePage open() {
        return PageFactory.newInstance(
                HomePage.class,
                "https://selenium.dev/");
    }

    public HeaderComponent getHeader() {
        // when using Selenium grid, the browser size is smaller and the header is hidden behind this link
        if (menuLink.isDisplayed()) {
            menuLink.click();
        }
        return header;
    }
}
