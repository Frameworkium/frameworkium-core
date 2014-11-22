package com.lazerycode.selenium.pages.web;

import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader;

import com.lazerycode.selenium.pages.internal.BasePage;
import com.lazerycode.selenium.pages.web.components.Header;

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
