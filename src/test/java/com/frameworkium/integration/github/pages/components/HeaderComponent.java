package com.frameworkium.integration.github.pages.components;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.PageFactory;
import com.frameworkium.core.ui.pages.Visibility;
import com.frameworkium.core.ui.tests.BaseUITest;
import com.frameworkium.integration.github.pages.ExplorePage;
import com.frameworkium.integration.github.pages.SearchResultsPage;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import ru.yandex.qatools.htmlelements.element.*;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@FindBy(css = "header")
public class HeaderComponent extends HtmlElement {

    @Visible
    @FindBy(css = "a.header-logo-invertocat")
    private Link homeLink;

    @FindBy(name = "q")
    private TextInput searchBox;

    @FindBy(css = "nav a[href='/explore']")
    private Link exploreLink;


    @Step("Go to the explore page")
    public ExplorePage clickExplore() {
        exploreLink.click();
        return PageFactory.newInstance(ExplorePage.class);
    }

    @Step("Search for the text '{searchText}'")
    public SearchResultsPage search(String searchText) {
        searchBox.sendKeys(searchText + Keys.ENTER);
        return PageFactory.newInstance(SearchResultsPage.class);
    }

    @Step("Testing Visibility.forceVisible()")
    public void testForceVisible() {
        Wait<WebDriver> wait = BaseUITest.newDefaultWait();

        WebElement link = homeLink.getWrappedElement();
        // hide the home link
        BaseUITest.getDriver().executeScript(
                "arguments[0].style.visibility='hidden';", link);
        wait.until(ExpectedConditions.not(visibilityOf(link)));
        // test force visible works
        new Visibility().forceVisible(link);
        wait.until(visibilityOf(link));
    }
}
