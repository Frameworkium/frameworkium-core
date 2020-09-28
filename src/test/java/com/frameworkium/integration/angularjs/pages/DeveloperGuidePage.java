package com.frameworkium.integration.angularjs.pages;

import com.frameworkium.lite.ui.ExtraExpectedConditions;
import com.frameworkium.lite.ui.annotations.Visible;
import com.frameworkium.lite.ui.pages.BasePage;
import com.frameworkium.lite.ui.pages.PageFactory;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Link;
import ru.yandex.qatools.htmlelements.element.TextInput;

public class DeveloperGuidePage extends BasePage<DeveloperGuidePage> {

    @Name("Developer guide search")
    @Visible
    @FindBy(css = "input[name='as_q']")
    private TextInput searchField;

    @Name("Bootstrap search item")
    @FindBy(linkText = "Bootstrap")
    private Link bootstrapSearchItem;

    @Name("Loading")
    @FindBy(id = "loading")
    private WebElement loading;

    public static DeveloperGuidePage open() {
        return PageFactory.newInstance(
                DeveloperGuidePage.class, "https://docs.angularjs.org/guide");
    }

    public DeveloperGuidePage searchDeveloperGuide(String inputText) {
        searchField.sendKeys(inputText);
        return this;
    }

    public DeveloperGuidePage clickBootstrapSearchItem() {
        bootstrapSearchItem.click();
        wait.until(ExtraExpectedConditions.notPresentOrInvisible(loading));
        return this;
    }

    public String getGuideTitle() {
        return getTitle();
    }
}
