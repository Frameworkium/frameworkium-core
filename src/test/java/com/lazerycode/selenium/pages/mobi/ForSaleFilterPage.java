package com.lazerycode.selenium.pages.mobi;

import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.TextInput;
import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader;

import com.lazerycode.selenium.pages.internal.BasePage;
import com.lazerycode.selenium.pages.internal.PageFactory;

public class ForSaleFilterPage extends BasePage<ForSaleFilterPage> {

    @Name("Find Properties button")
    @FindBy(id = "search")
    private Button findPropertiesButton;

    @Name("Location field")
    @FindBy(id = "searchLocation")
    private TextInput locationField;
    
    public ForSaleFilterPage get() {
        HtmlElementLoader.populatePageObject(this, driver);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("search")));
        return this;
    }

    @Step("Enter location \"{0}\".")
    public ForSaleFilterPage enterLocation(String location) {
        locationField.sendKeys(location);
        return this;
    }
    
    @Step("Click Find Properties.")
    public ForSaleResultsPage findProperties() {
        findPropertiesButton.click();
        return PageFactory.getInstance(ForSaleResultsPage.class);
    }
}
