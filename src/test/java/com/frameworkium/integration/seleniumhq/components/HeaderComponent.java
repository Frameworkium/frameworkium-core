package com.frameworkium.integration.seleniumhq.components;

import com.frameworkium.lite.ui.UITestLifecycle;
import com.frameworkium.lite.ui.pages.PageFactory;
import com.frameworkium.integration.seleniumhq.pages.SeleniumDownloadPage;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.Link;

@FindBy(id = "header")
public class HeaderComponent extends HtmlElement {

    @FindBy(css = "a.nav-item:nth-child(3)")
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
