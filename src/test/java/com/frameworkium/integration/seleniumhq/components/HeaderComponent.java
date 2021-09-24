package com.frameworkium.integration.seleniumhq.components;

import com.frameworkium.core.htmlelements.element.HtmlElement;
import com.frameworkium.core.htmlelements.element.Link;
import com.frameworkium.core.ui.UITestLifecycle;
import com.frameworkium.core.ui.pages.PageFactory;
import com.frameworkium.integration.seleniumhq.pages.SeleniumDownloadPage;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;


@FindBy(className = "navbar")
public class HeaderComponent extends HtmlElement {

    @FindBy(css = "#main_navbar [href='/downloads']")
    private Link downloadLink;

    public SeleniumDownloadPage clickDownloadLink() {
        UITestLifecycle.get().getWait().until(ExpectedConditions.elementToBeClickable(downloadLink));
        downloadLink.click();
        return PageFactory.newInstance(SeleniumDownloadPage.class);
    }

}
