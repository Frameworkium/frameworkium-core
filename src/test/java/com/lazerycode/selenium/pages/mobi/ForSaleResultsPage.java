package com.lazerycode.selenium.pages.mobi;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader;

import com.lazerycode.selenium.pages.internal.BasePage;
import com.lazerycode.selenium.pages.mobi.components.Header;

public class ForSaleResultsPage extends BasePage<ForSaleResultsPage> {

    private Header searchResultsHeader;

    public ForSaleResultsPage get() {
        HtmlElementLoader.populatePageObject(this, driver);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("saveSearchAndAlertButton")));
        return this;
    }

    public Header header() {
        return searchResultsHeader;
    }

}
