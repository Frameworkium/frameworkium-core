package com.bootstrapium.pages.app;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.TextBlock;
import ru.yandex.qatools.htmlelements.element.TextInput;
import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader;

import com.bootstrapium.pages.internal.BasePage;
import com.bootstrapium.pages.internal.PageFactory;

public class CalculatorPage extends BasePage<CalculatorPage>{

    @FindBy(className = "UIATextField")
    private List<TextInput> fields;
    
    @FindBy(className = "UIAButton")
    private Button computeSumButton;
    
    @FindBy(className = "UIAStaticText")
    private TextBlock resultLabel;
    
    public CalculatorPage get() {
        HtmlElementLoader.populatePageObject(this, driver);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("UIAButton")));
        return this;
    }
    
    public static CalculatorPage newInstance() {
        return PageFactory.getInstance(CalculatorPage.class);
    }
    
    @Step("Compute sum of {0} and {1}.")
    public CalculatorPage computeSum(Integer a, Integer b) {
        fields.get(0).sendKeys(a.toString());
        fields.get(1).sendKeys(b.toString());
        computeSumButton.click();
        return this;
    }
 
    @Step("Get the result.")
    public String getResult() {
        return resultLabel.getText();
    }
}
