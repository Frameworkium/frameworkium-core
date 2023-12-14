package com.frameworkium.integration.seleniumhq.components;

import com.frameworkium.lite.ui.UITestLifecycle;
import com.frameworkium.lite.ui.pages.PageFactory;
import com.frameworkium.integration.seleniumhq.pages.SeleniumDownloadPage;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.FindBy;
import com.frameworkium.lite.htmlelements.element.HtmlElement;
import com.frameworkium.lite.htmlelements.element.Link;

@FindBy(css = "header")
public class HeaderComponent extends HtmlElement {

    @FindBy(xpath = "//a/span[contains(text(),'Download')]")
    private Link downloadLink;

    public SeleniumDownloadPage clickDownloadLink() {
        // Make sure the window is big enough to see the link
        // Especially important for headless browsers
        UITestLifecycle.get().getWebDriver()
                .manage().window().setSize(new Dimension(1200, 900));
        downloadLink.click();
        return PageFactory.newInstance(SeleniumDownloadPage.class);
    }

}
