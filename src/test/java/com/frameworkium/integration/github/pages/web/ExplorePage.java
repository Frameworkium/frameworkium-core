package com.frameworkium.integration.github.pages.web;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import com.frameworkium.integration.github.pages.web.components.HeaderComponent;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.annotations.Name;

public class ExplorePage extends BasePage<ExplorePage> {

    @Visible
    @Name("Header")
    private HeaderComponent header;

    @Visible
    @Name("Explore Content")
    @FindBy(css = "div.explore-marketing-header")
    private WebElement marketingBanner;

    public HeaderComponent theHeader() {
        return header;
    }
}
