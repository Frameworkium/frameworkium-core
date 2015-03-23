package com.frameworkium.pages.web.the_internet.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.Visible;

public class BasicAuthSuccessPage extends BasePage<BasicAuthSuccessPage> {

    @Visible
    @Name("Header text")
    @FindBy(css = "div.example h3")
    private WebElement headerText;

    @Step("Return the text displayed in the header")
    public String getHeaderText() {
       return headerText.getText();
    }
}
