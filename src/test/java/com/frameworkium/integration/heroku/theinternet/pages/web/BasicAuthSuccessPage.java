package com.frameworkium.integration.heroku.theinternet.pages.web;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;

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
