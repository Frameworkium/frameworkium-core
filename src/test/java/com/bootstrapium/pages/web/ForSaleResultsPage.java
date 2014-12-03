package com.bootstrapium.pages.web;

import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader;

import com.bootstrapium.pages.internal.BasePage;
import com.bootstrapium.pages.web.components.Header;

public class ForSaleResultsPage extends BasePage<ForSaleResultsPage> {

    @FindBy(id = "siteheader")
    private Header header;

    public ForSaleResultsPage get() {
        HtmlElementLoader.populatePageObject(this, driver);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("numberOfProperties")));
        return this;
    }

    public Header header() {
        return header;
    }

}
