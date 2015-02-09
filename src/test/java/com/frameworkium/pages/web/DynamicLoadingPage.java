package com.frameworkium.pages.web;

import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Link;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.PageFactory;
import com.frameworkium.pages.internal.Visible;

public class DynamicLoadingPage extends BasePage<DynamicLoadingPage> {

    @Visible
    @Name("Example 1: Element on page that is hidden link")
    @FindBy(linkText = "Example 1: Element on page that is hidden")
    private Link example1Link;
    
    @Name("Example 2: Element rendered after the fact")
    @FindBy(linkText = "Example 2: Element rendered after the fact")
    private Link example2Link;

    @Step("Click the 'Example 1: Element on page that is hidden' link")
    public DynamicLoadingExamplePage clickExample1() {
        example1Link.click();
        return PageFactory.newInstance(DynamicLoadingExamplePage.class);
    }
    
    @Step("Click the 'Example 2: Element rendered after the fact' link")
    public DynamicLoadingExamplePage clickExample2() {
        example2Link.click();
        return PageFactory.newInstance(DynamicLoadingExamplePage.class);
    }
}
