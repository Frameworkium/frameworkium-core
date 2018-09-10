package com.frameworkium.integration.theinternet.pages;

import com.frameworkium.core.ui.annotations.Invisible;
import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import com.frameworkium.core.ui.pages.PageFactory;
import io.qameta.allure.Step;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.annotations.Timeout;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

public class DynamicLoadingExamplePage extends BasePage<DynamicLoadingExamplePage> {

    @Visible
    @Name("Start button")
    @FindBy(css = "#start button")
    private Button startButton;

    @Invisible
    @Timeout(0) // prevents page load taking 5s due to implicit timeout
    @Name("Hidden element")
    @FindBy(id = "finish")
    private HtmlElement dynamicElement;

    public static DynamicLoadingExamplePage openExampleTwo() {
        return PageFactory.newInstance(
                DynamicLoadingExamplePage.class,
                "http://the-internet.herokuapp.com/dynamic_loading/2");
    }

    @Step("Click Start")
    public DynamicLoadingExamplePage clickStart() {
        startButton.click();
        return this;
    }

    public String getElementText() {
        wait.until(visibilityOf(dynamicElement));
        return dynamicElement.getText();
    }

}
