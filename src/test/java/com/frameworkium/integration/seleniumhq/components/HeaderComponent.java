package com.frameworkium.integration.seleniumhq.components;

import com.frameworkium.core.htmlelements.element.HtmlElement;
import com.frameworkium.core.htmlelements.element.Link;
import com.frameworkium.core.ui.pages.PageFactory;
import com.frameworkium.integration.seleniumhq.pages.SeleniumDownloadPage;
import org.openqa.selenium.support.FindBy;


@FindBy(className = "navbar")
public class HeaderComponent extends HtmlElement {

    @FindBy(css = "#main_navbar [href='/downloads']")
    private Link downloadLink;

    public SeleniumDownloadPage clickDownloadLink() {
        downloadLink.click();
        return PageFactory.newInstance(SeleniumDownloadPage.class);
    }

}
