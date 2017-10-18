package com.frameworkium.integration.github.pages.web;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import com.frameworkium.integration.github.pages.web.components.HeaderComponent;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.htmlelements.annotations.Name;

public class ExplorePage extends BasePage<ExplorePage> {

    @Visible
    @Name("Header")
    private HeaderComponent header;

    @Visible
    private WebElement trending;

    public HeaderComponent theHeader() {
        return header;
    }
}
