package com.frameworkium.pages.web.the_internet.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Link;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.Visible;

public class SecureFileDownloadPage extends BasePage<SecureFileDownloadPage> {

    @Visible
    @Name("Heading")
    @FindBy(css = "div.example h3")
    private WebElement heading;

    @Visible
    @Name("First download link")
    @FindBy(css = "div.example a:first-of-type")
    private Link firstDownloadLink;

    public String getHeadingText() {
        return heading.getText();
    }

}
