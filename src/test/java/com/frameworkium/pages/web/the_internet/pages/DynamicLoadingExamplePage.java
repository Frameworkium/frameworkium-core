package com.frameworkium.pages.web.the_internet.pages;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.Visible;

public class DynamicLoadingExamplePage extends BasePage<DynamicLoadingExamplePage> {

    @Visible
    @Name("Start button")
    @FindBy(css = "#start button")
    private Button startButton;

    @Name("Hidden element")
    @FindBy(id = "finish")
    private HtmlElement hiddenElement;

    @Step("Click Start")
    public DynamicLoadingExamplePage clickStart() {
        startButton.click();
        wait.until(ExpectedConditions.visibilityOf(hiddenElement));
        return this;
    }

    @Step("Wait for the hidden element to be displayed")
    public DynamicLoadingExamplePage waitForElementToBeDisplayed() {
        wait.until(ExpectedConditions.visibilityOf(hiddenElement));
        return this;
    }

    public boolean isElementDisplayed() {
        return hiddenElement.isDisplayed();
    }

    public boolean isElementPresent() {
        try {
            hiddenElement.getTagName();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
