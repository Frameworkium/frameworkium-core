package com.frameworkium.integration.seleniumhq.pages;

import com.frameworkium.lite.ui.annotations.Visible;
import com.frameworkium.lite.ui.pages.BasePage;
import com.frameworkium.integration.seleniumhq.components.HeaderComponent;
import org.openqa.selenium.support.CacheLookup;

public class HomePage extends BasePage<HomePage> {

    @CacheLookup
    @Visible
    private HeaderComponent header;

    public static HomePage open() {
        return new HomePage().get("https://selenium.dev/");
    }

    public HeaderComponent getHeader() {
        return header;
    }
}
