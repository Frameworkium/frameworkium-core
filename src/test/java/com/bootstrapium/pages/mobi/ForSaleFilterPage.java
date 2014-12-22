package com.bootstrapium.pages.mobi;

import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.TextInput;
import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader;

import com.bootstrapium.pages.internal.BasePage;
import com.bootstrapium.pages.internal.PageFactory;
import com.bootstrapium.pages.internal.Visible;

public class ForSaleFilterPage extends BasePage<ForSaleFilterPage> {

    @Visible
    @Name("Find Properties button")
    @FindBy(id = "search")
    private Button findPropertiesButton;

    @Name("Location field")
    @FindBy(id = "searchLocation")
    private TextInput locationField;
    
    @Step("Enter location \"{0}\".")
    public ForSaleFilterPage enterLocation(String location) {
        locationField.sendKeys(location);
        return this;
    }
    
    @Step("Click Find Properties.")
    public ForSaleResultsPage findProperties() {
        findPropertiesButton.click();
        return PageFactory.newInstance(ForSaleResultsPage.class);
    }
}
