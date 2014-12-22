package com.bootstrapium.pages.web;

import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Button;

import com.bootstrapium.pages.internal.BasePage;
import com.bootstrapium.pages.internal.PageFactory;
import com.bootstrapium.pages.internal.Visible;

public class ForSaleFilterPage extends BasePage<ForSaleFilterPage> {

    @Visible
    @Name("Find Properties button")
    @FindBy(id = "submit")
    private Button findPropertiesButton;

    @Step("Click Find Properties.")
    public ForSaleResultsPage findProperties() {
        findPropertiesButton.click();
        return PageFactory.newInstance(ForSaleResultsPage.class);
    }
}
